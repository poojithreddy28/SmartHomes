import openai
import mysql.connector
import json
import time
from pymongo import MongoClient
from datetime import datetime
import random
import os

# OpenAI API Key
openai.api_key = os.getenv("OPENAI_API_KEY")

# MySQL Database Configuration
mysql_config = {
    "host": "localhost",
    "user": "poojith",
    "password": "poojith",
    "database": "smarthomes"
}

# MongoDB Configuration
mongodb_client = MongoClient("mongodb://localhost:27017/")
mongodb = mongodb_client["SmartHomes"]  # Ensure this matches the database name
reviews_collection = mongodb["reviews"]

# Categories for Products
categories = [
    "Smart Locks",
    "Smart Speakers",
    "Smart Lightings",
    "Smart Thermostats",
    "Smart Doorbells"
]

# Retry mechanism for OpenAI API calls
def retry_on_failure(func, retries=3, delay=2):
    for attempt in range(retries):
        try:
            return func()
        except Exception as e:
            print(f"Attempt {attempt + 1} failed: {e}")
            if attempt < retries - 1:
                time.sleep(delay * (2 ** attempt))  # Exponential backoff
            else:
                raise

# Function to generate products using OpenAI GPT-4o-mini
def generate_products():
    products = []
    for category in categories:
        prompt = f"""
        Generate 3 unique products in the category '{category}' with the following details in JSON array format:
        [
            {{
                "name": "Product Name",
                "price": "Product Price",
                "category": "{category}",
                "description": "Product Description (max 80 words)"
            }},
            {{
                "name": "Product Name",
                "price": "Product Price",
                "category": "{category}",
                "description": "Product Description (max 80 words)"
            }},
            {{
                "name": "Product Name",
                "price": "Product Price",
                "category": "{category}",
                "description": "Product Description (max 80 words)"
            }}
        ]
        """
        try:
            response = retry_on_failure(lambda: openai.ChatCompletion.create(
                model="gpt-4o-mini",
                messages=[{"role": "user", "content": prompt}]
            ))

            raw_content = response["choices"][0]["message"]["content"].strip()
            print("Raw Content:", raw_content)

            products_list = json.loads(raw_content.strip("```json").strip("```").strip())
            for product in products_list:
                products.append(product)

        except json.JSONDecodeError as e:
            print(f"Error decoding JSON response: {e}")
            print("Raw Content:", raw_content)
        except Exception as e:
            print(f"Unexpected error: {e}")
    return products

# Function to insert products into MySQL
def insert_products_into_mysql(products):
    try:
        db = mysql.connector.connect(**mysql_config)
        cursor = db.cursor()

        query = """
        INSERT INTO Products (name, price, category, description, image_path, discount, warranty, manufacturer_rebate, quantity)
        VALUES (%s, %s, %s, %s, NULL, NULL, NULL, NULL, NULL)
        """
        for product in products:
            name = product["name"]
            price = float(product["price"].replace("$", "").strip())
            category = product["category"]
            description = product["description"]

            # Insert product into MySQL without generating an image
            cursor.execute(query, (name, price, category, description))

        db.commit()
        cursor.close()
        db.close()
        print(f"{len(products)} products successfully inserted into MySQL.")
    except Exception as e:
        print(f"Error inserting products into MySQL: {e}")

# Function to generate reviews for products using OpenAI
def generate_reviews(products):
    for product in products:
        category = product["category"]
        reviews = []

        for _ in range(5):  # Generate 5 reviews per product
            prompt = f"""
            Generate a product review for the following product:
            - Product Name: {product['name']}
            - Product Category: {category}
            - Product Price: {product['price']}
            Provide the review in the following JSON format:
            {{
                "reviewText": "The product is <aspect>. <Elaborate review>",
                "reviewRating": "<1 to 5>",
                "userAge": "<18 to 70>",
                "userGender": "<Male/Female/Other>",
                "userOccupation": "<Student/Professional/Retired>"
            }}
            """
            try:
                response = retry_on_failure(lambda: openai.ChatCompletion.create(
                    model="gpt-4o-mini",
                    messages=[{"role": "user", "content": prompt}]
                ))

                raw_content = response["choices"][0]["message"]["content"].strip()
                print("Raw Review Content:", raw_content)

                review_data = json.loads(raw_content.strip("```json").strip("```").strip())

                review = {
                    "storeID": "Home Delivery",
                    "manufacturerRebate": random.choice(["Yes", "No"]),
                    "userId": "Poojith reddy",
                    "reviewRating": int(review_data["reviewRating"]),
                    "userAge": int(review_data["userAge"]),
                    "productCategory": category,
                    "productOnSale": random.choice(["Yes", "No"]),
                    "reviewDate": datetime.now().strftime("%Y-%m-%d"),
                    "userOccupation": review_data["userOccupation"],
                    "storeZip": "60616",
                    "productModelName": product["name"],
                    "storeState": "IL",
                    "userGender": review_data["userGender"],
                    "storeCity": "Chicago",
                    "productPrice": product["price"],
                    "reviewText": review_data["reviewText"]
                }
                reviews.append(review)

            except json.JSONDecodeError as e:
                print(f"Error decoding JSON review response: {e}")
                print("Raw Content:", raw_content)
            except Exception as e:
                print(f"Unexpected error generating review: {e}")

        if reviews:
            reviews_collection.insert_many(reviews)
            print(f"Inserted 5 reviews for product '{product['name']}' into MongoDB.")

# Function to fetch and print inserted reviews from MongoDB
def fetch_and_print_reviews():
    try:
        reviews_cursor = reviews_collection.find()
        reviews = []
        for review in reviews_cursor:
            reviews.append({
                "Product Name": review.get("productModelName"),
                "Category": review.get("productCategory"),
                "Review Rating": review.get("reviewRating"),
                "User Age": review.get("userAge"),
                "User Gender": review.get("userGender"),
                "User Occupation": review.get("userOccupation"),
                "Review Text": review.get("reviewText"),
                "Review Date": review.get("reviewDate"),
                "Store City": review.get("storeCity"),
                "Store State": review.get("storeState"),
                "Product Price": review.get("productPrice")
            })

        print("\nFetched Reviews:")
        for review in reviews:
            print(json.dumps(review, indent=4))
        
        print(f"\nTotal Reviews Fetched: {len(reviews)}")
    except Exception as e:
        print(f"Error fetching reviews from MongoDB: {e}")

# Main function
def main():
    try:
        print("Generating products using OpenAI GPT-4o-mini...")
        products = generate_products()

        if not products:
            print("No products generated.")
            return

        print("Inserting products into MySQL...")
        insert_products_into_mysql(products)

        print("Generating reviews for products...")
        generate_reviews(products)

        print("Fetching and displaying reviews from MongoDB...")
        fetch_and_print_reviews()

    except Exception as e:
        print(f"An error occurred: {e}")

if __name__ == "__main__":
    main()