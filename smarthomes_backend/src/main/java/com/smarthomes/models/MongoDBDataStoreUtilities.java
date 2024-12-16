
package com.smarthomes.models;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.json.JSONObject;

public class MongoDBDataStoreUtilities {

    private static MongoClient mongoClient = null;

    // Initialize MongoDB connection
    private static MongoDatabase getDatabase() {
        System.out.println("MongoDB connection established");
        if (mongoClient == null) {
            String uri = "mongodb://localhost:27017"; // Replace with your MongoDB connection string if needed
            mongoClient = MongoClients.create(uri); // Use MongoClients.create() with the connection string
            System.out.println("MongoDB connection established");
        }
        return mongoClient.getDatabase("SmartHomes"); // Use your database name
    }

    // Method to save the review details into MongoDB
    public static boolean saveReview(JSONObject reviewJson) {
        try {
            // Get the MongoDB database and collection
            MongoDatabase db = getDatabase();
            MongoCollection<Document> collection = db.getCollection("reviews"); // Use your collection name "reviews"

            // Convert the incoming JSONObject to a BSON Document for MongoDB
            Document reviewDoc = Document.parse(reviewJson.toString());

            // Insert the review document into the collection
            collection.insertOne(reviewDoc);

            System.out.println("Review successfully inserted into MongoDB");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error inserting review into MongoDB");
            return false;
        }
    }

   public static org.json.JSONArray getReviewsByProductName(String productModelName) {
    MongoDatabase db = getDatabase();
    MongoCollection<Document> collection = db.getCollection("reviews");

    List<Document> reviews = collection.find(new Document("productModelName", productModelName)).into(new ArrayList<>());
    org.json.JSONArray reviewsArray = new org.json.JSONArray();

    for (Document doc : reviews) {
        JSONObject reviewJson = new JSONObject(doc.toJson());
        reviewsArray.put(reviewJson);
    }

    return reviewsArray;
}
}