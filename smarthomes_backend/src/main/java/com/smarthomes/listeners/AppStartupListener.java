package com.smarthomes.listeners;

import com.smarthomes.models.Product;
import com.smarthomes.models.MySQLDataStoreUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;

public class AppStartupListener implements ServletContextListener {

    private static final String PRODUCT_CATALOG_XML = "/Users/sruthisanjana/Desktop/EWA/Sep25_OrderLeft/Archive 2/smarthomes_backend/src/main/resources/ProductCatalog.xml"; // Replace with actual path
    private static final String PRODUCT_SERIALIZED_FILE = "/Users/sruthisanjana/Desktop/EWA/Sep25_OrderLeft/Archive 2/smarthomes_backend/products.ser"; // Replace with actual path

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // Step 1: Check if products are already present in the database
            if (MySQLDataStoreUtilities.isProductsTableEmpty()) {
                System.out.println("Products table is empty. Loading from XML.");

                // Step 2: Load products from XML into a HashMap
                HashMap<Integer, Product> products = loadProductsFromXML();

                // Step 3: Serialize the HashMap to a .ser file
                serializeProducts(products);

                // Step 4: Insert products into MySQL database
                insertProductsIntoDatabase(products);

                System.out.println("Products loaded and inserted into database successfully.");
            } else {
                System.out.println("Products already exist in the database.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Handle cleanup if needed
    }

    private HashMap<Integer, Product> loadProductsFromXML() throws Exception {
        HashMap<Integer, Product> products = new HashMap<>();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new File(PRODUCT_CATALOG_XML));

        NodeList productList = doc.getElementsByTagName("product");

        for (int i = 0; i < productList.getLength(); i++) {
            Element productElement = (Element) productList.item(i);

            int id = Integer.parseInt(productElement.getElementsByTagName("id").item(0).getTextContent());
            String name = productElement.getElementsByTagName("name").item(0).getTextContent();
            double price = Double.parseDouble(productElement.getElementsByTagName("price").item(0).getTextContent());
            String category = productElement.getElementsByTagName("category").item(0).getTextContent();
            String description = productElement.getElementsByTagName("description").item(0).getTextContent();
            String imagePath = productElement.getElementsByTagName("imagePath").item(0).getTextContent();
            double discount = Double.parseDouble(productElement.getElementsByTagName("discount").item(0).getTextContent());
            int warranty = Integer.parseInt(productElement.getElementsByTagName("warranty").item(0).getTextContent());
            double manufacturerRebate = Double.parseDouble(productElement.getElementsByTagName("manufacturerRebate").item(0).getTextContent());
            int quantity = Integer.parseInt(productElement.getElementsByTagName("quantity").item(0).getTextContent());

            Product product = new Product(id, name, price, category, description, imagePath, discount, warranty, manufacturerRebate, quantity);
            products.put(id, product);
        }

        return products;
    }

    private void serializeProducts(HashMap<Integer, Product> products) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PRODUCT_SERIALIZED_FILE))) {
            oos.writeObject(products);
        }
        System.out.println("Products serialized successfully.");
    }

    private void insertProductsIntoDatabase(HashMap<Integer, Product> products) throws Exception {
        try (Connection conn = MySQLDataStoreUtilities.getConnection()) {
            String sql = "INSERT INTO products (id, name, price, category, description, image_path, discount, warranty, manufacturer_rebate, quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            for (Product product : products.values()) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, product.getId());
                    ps.setString(2, product.getName());
                    ps.setDouble(3, product.getPrice());
                    ps.setString(4, product.getCategory());
                    ps.setString(5, product.getDescription());
                    ps.setString(6, product.getImage_path());
                    ps.setDouble(7, product.getDiscount());
                    ps.setInt(8, product.getWarranty());
                    ps.setDouble(9, product.getManufacturerRebate());
                    ps.setInt(10, product.getQuantity());
                    ps.executeUpdate();
                }
            }
        }
        System.out.println("Products inserted into database successfully.");
    }
}