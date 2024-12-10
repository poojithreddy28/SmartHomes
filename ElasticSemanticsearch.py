from flask import Flask, request, jsonify
from elasticsearch import Elasticsearch
import openai
import mysql.connector
from flask_cors import CORS
from pymongo import MongoClient
import os  # For environment variable checks

# Flask App
app = Flask(__name__)
# Enable CORS
CORS(app, resources={r"/*": {"origins": "*"}})

# Elasticsearch Setup
es = Elasticsearch("http://localhost:9200")

# OpenAI API Key
openai.api_key = os.getenv("OPENAI_API_KEY")
# Elasticsearch Index Names
PRODUCT_INDEX = "products_embeddings"
REVIEWS_INDEX = "reviews_embeddings"

# MongoDB Configuration
mongo_client = MongoClient("mongodb://localhost:27017/")
mongo_db = mongo_client["SmartHomes"]
reviews_collection = mongo_db["reviews"]

# MySQL Database Configuration
db_config = {
    "host": "localhost",
    "user": "poojith",
    "password": "poojith",
    "database": "smarthomes"
}

# Step 1: Create Index in Elasticsearch
def create_index(index_name, dims=1536, is_reviews=False):
    try:
        # Delete the index if it exists
        if es.indices.exists(index=index_name):
            es.indices.delete(index=index_name)

        # Create the index with the appropriate mapping
        index_mapping = {
            "mappings": {
                "properties": {
                    "id": {"type": "keyword" if is_reviews else "integer"},
                    "name": {"type": "text"} if not is_reviews else None,
                    "description": {"type": "text"} if not is_reviews else None,
                    "category": {"type": "text"} if not is_reviews else None,  # Added category for products
                    "review_text": {"type": "text"} if is_reviews else None,
                    "embedding": {"type": "dense_vector", "dims": dims}  # 1536 is the embedding size
                }
            }
        }

        # Remove None fields
        index_mapping["mappings"]["properties"] = {k: v for k, v in index_mapping["mappings"]["properties"].items() if v}

        es.indices.create(index=index_name, body=index_mapping)
        print(f"Index '{index_name}' created in Elasticsearch.")
    except Exception as e:
        print(f"Error creating index '{index_name}': {e}")

# Step 2: Generate Embeddings
def generate_embedding(text):
    response = openai.Embedding.create(
        input=text,
        model="text-embedding-3-small"
    )
    return response["data"][0]["embedding"]

# Step 3: Store Product Embeddings
def store_product_embeddings():
    try:
        db = mysql.connector.connect(**db_config)
        cursor = db.cursor(dictionary=True)

        cursor.execute("SELECT id, name, description, price, category FROM Products")
        products = cursor.fetchall()

        for product in products:
            product_id = product["id"]
            name = product["name"]
            description = product["description"]
            price = product["price"]
            category = product["category"]

            embedding = generate_embedding(description)

            doc = {
                "id": product_id,
                "name": name,
                "description": description,
                "price": price,
                "category": category,
                "embedding": embedding
            }
            es.index(index=PRODUCT_INDEX, id=product_id, document=doc)

        print("Product embeddings stored successfully.")
    except Exception as e:
        print(f"Error storing product embeddings: {e}")

# Step 4: Store Review Embeddings
def store_review_embeddings():
    try:
        reviews = list(reviews_collection.find({}))

        for review in reviews:
            review_id = str(review["_id"])
            review_text = review["reviewText"]
            product_name = review.get("productModelName", "Unknown Product")
            rated_by = review.get("userId", "Anonymous")  # User who rated the review
            rating = review.get("reviewRating", 0)  # Rating given to the review

            embedding = generate_embedding(review_text)

            doc = {
                "id": review_id,
                "review_text": review_text,
                "product_name": product_name,
                "rated_by": rated_by,  # Store who rated the review
                "rating": rating,  # Store the rating
                "embedding": embedding
            }
            es.index(index=REVIEWS_INDEX, id=review_id, document=doc)

        print("Review embeddings stored successfully.")
    except Exception as e:
        print(f"Error storing review embeddings: {e}")

# Step 6: Semantic Search on Reviews
@app.route("/search_reviews", methods=["POST"])
def search_reviews():
    try:
        query = request.json.get("query", "")
        query_embedding = generate_embedding(query)

        search_query = {
            "query": {
                "script_score": {
                    "query": {"match_all": {}},
                    "script": {
                        "source": "cosineSimilarity(params.query_vector, 'embedding') + 1.0",
                        "params": {"query_vector": query_embedding}
                    }
                }
            }
        }

        response = es.search(index=REVIEWS_INDEX, body=search_query, size=50)
        seen_product_names = set()  # To track unique product names in reviews
        results = []
        for hit in response["hits"]["hits"]:
            product_name = hit["_source"].get("product_name", "Unknown Product")  # Use the product name as the unique identifier
            if product_name not in seen_product_names:
                seen_product_names.add(product_name)
                results.append({
                    "review_text": hit["_source"]["review_text"],
                    "product_name": product_name,
                    "rated_by": hit["_source"].get("rated_by", "Anonymous"),  # Include rated by
                    "rating": hit["_source"].get("rating", 0),  # Include rating
                    "score": hit["_score"]
                })
            if len(results) >= 5:  # Limit to 5 unique results
                break

        return jsonify({"results": results}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    
# Step 5: Semantic Search on Products
@app.route("/search_products", methods=["POST"])
def search_products():
    try:
        query = request.json.get("query", "")
        query_embedding = generate_embedding(query)

        search_query = {
            "query": {
                "script_score": {
                    "query": {"match_all": {}},
                    "script": {
                        "source": "cosineSimilarity(params.query_vector, 'embedding') + 1.0",
                        "params": {"query_vector": query_embedding}
                    }
                }
            }
        }

        response = es.search(index=PRODUCT_INDEX, body=search_query)
        seen_names = set()  # To track unique product names
        results = []
        for hit in response["hits"]["hits"]:
            product_name = hit["_source"]["name"]  # Use the product name as the unique identifier
            if product_name not in seen_names:
                seen_names.add(product_name)
                results.append({
                    "name": product_name,
                    "description": hit["_source"]["description"],
                    "price": hit["_source"].get("price", "N/A"),
                    "category": hit["_source"].get("category", "N/A"),
                    "score": hit["_score"]
                })
            if len(results) >= 5:  # Limit to 5 unique results
                break

        return jsonify({"results": results}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route('/get_product_id', methods=['GET'])
def get_product_id():
    product_name = request.args.get('name')
    if not product_name:
        return jsonify({'error': 'Product name is required'}), 400

    # Establish a new database connection inside the function
    try:
        db = mysql.connector.connect(**db_config)
        cursor = db.cursor(dictionary=True)
        
        # Query your database to fetch the product ID by name
        cursor.execute("SELECT id FROM Products WHERE name = %s", (product_name,))
        product = cursor.fetchone()

        cursor.close()
        db.close()  # Always close the connection after use

        if product:
            return jsonify({'productId': product['id']}), 200
        else:
            return jsonify({'error': 'Product not found'}), 404
    except mysql.connector.Error as err:
        return jsonify({'error': f'Database error: {err}'}), 500

# Step 7: Initialize Indices and Store Embeddings
def initialize_indices():
    print("Initializing Elasticsearch indices...")
    create_index(PRODUCT_INDEX)
    create_index(REVIEWS_INDEX, is_reviews=True)
    print("Storing product embeddings...")
    store_product_embeddings()
    print("Storing review embeddings...")
    store_review_embeddings()

initialize_indices()

if __name__ == "__main__":
    app.run(port=5000)