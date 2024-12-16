package com.smarthomes.models;


import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class MySQLDataStoreUtilities {

    // Define your database configuration here (or load it from a configuration
    // file)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/smarthomes"; // Update with your DB details
    private static final String DB_USER = "poojith"; // Your MySQL username
    private static final String DB_PASSWORD = "poojith"; // Your MySQL password

    // Method to establish a database connection
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Register JDBC driver (you can also configure this in your project
            // dependencies)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Open a connection
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return conn;
    }

    // Method to close database connection and other resources
   

    public boolean saveCustomer(String fullName, String email, String password, String role, String street, String city, String state, String zipCode) {
        String insertCustomerQuery = "INSERT INTO customers (full_name, email, password, role, street, city, state, zip_code) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(insertCustomerQuery)) {
    
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, role);
            ps.setString(5, street);
            ps.setString(6, city);
            ps.setString(7, state);
            ps.setString(8, zipCode);
            ps.executeUpdate();
            return true;
    
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String validateUser(String email, String password, String role) {
        String selectQuery = "SELECT full_name FROM customers WHERE email = ? AND password = ? AND role = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(selectQuery)) {

            ps.setString(1, email);
            ps.setString(2, password);
            ps.setString(3, role);
            System.out.println("Executing query with: " + email + ", " + password + ", " + role);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("full_name"); // Return the full name if user is validated
                }
            }
            

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if validation fails
    }


   // Add product with image path
   public boolean addProduct(String name, double price, String category, String description, String imagePath, double discount, int warranty, double manufacturerRebate, int quantity) {
    String insertProductQuery = "INSERT INTO products (name, price, category, description, image_path, discount, warranty, manufacturer_rebate, quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(insertProductQuery)) {
        ps.setString(1, name);
        ps.setDouble(2, price);
        ps.setString(3, category);
        ps.setString(4, description);
        ps.setString(5, imagePath);
        ps.setDouble(6, discount); // Insert discount
        ps.setInt(7, warranty); // Insert warranty
        ps.setDouble(8, manufacturerRebate); // Insert rebate
        ps.setInt(9, quantity); // Insert quantity

        ps.executeUpdate();
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }

}

// Update product with image path
public boolean updateProduct(int id, String name, double price, String category, String description) {
    String updateProductQuery = "UPDATE products SET name = ?, price = ?, category = ?, description = ?WHERE id = ?";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(updateProductQuery)) {
        ps.setString(1, name);
        ps.setDouble(2, price);
        ps.setString(3, category);
        ps.setString(4, description);
        
        ps.setInt(6, id);
        ps.executeUpdate();
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

// Get all products from the database, including image path
public List<Product> getAllProducts() {
    List<Product> products = new ArrayList<>();
    String query = "SELECT * FROM products";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(query);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            Product product = new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getString("category"),
                rs.getString("description"),
                rs.getString("image_path")  // Get image path from DB
            );
            products.add(product);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return products;
}

// Get products by category, including image path
public List<Product> getProductsByCategory(String category) {
    List<Product> products = new ArrayList<>();
    String query = "SELECT * FROM products WHERE category = ?";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {
        ps.setString(1, category);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Product product = new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getString("category"),
                    rs.getString("description"),
                    rs.getString("image_path")  // Get image path from DB
                );
                products.add(product);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return products;
}

// Delete product by ID
public boolean deleteProduct(int id) {
    String query = "DELETE FROM products WHERE id = ?";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {
        ps.setInt(1, id);
        ps.executeUpdate();
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

public Product getProductById(int productId) {
    Product product = null;
    String query = "SELECT * FROM Products WHERE id = ?"; // Assuming "Products" is your table name

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {
        ps.setInt(1, productId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                product = new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getString("category"),
                    rs.getString("description"),
                    rs.getString("image_path")
                );
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return product;
}

// Method to place an order
public boolean placeOrder(int userId, String customerName, String customerAddress, String creditCardNumber, double totalSales, String deliveryType, int storeId, String storeAddress, HashMap<String, Cart> userCart, Timestamp purchaseDate, Timestamp shipDate, Timestamp deliveryDate) {
    try (Connection conn = getConnection()) {
        // Insert the order into Orders table
        String insertOrderSQL;

        // If storeId is 0, we assume it's Home Delivery
        if (storeId == 0) {
            insertOrderSQL = "INSERT INTO Orders (user_id, customer_name, customer_address, credit_card_number, purchase_date, ship_date, delivery_date, order_status, total_sales, delivery_type, shipping_cost) VALUES (?, ?, ?, ?, ?, ?, ?, 'processing', ?, ?, 50.00)";
        } else {
            insertOrderSQL = "INSERT INTO Orders (user_id, customer_name, customer_address, credit_card_number, purchase_date, ship_date, delivery_date, order_status, total_sales, delivery_type, shipping_cost, store_id, store_address) VALUES (?, ?, ?, ?, ?, ?, ?, 'processing', ?, ?, 50.00, ?, ?)";
        }

        PreparedStatement psOrder = conn.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS);
        psOrder.setInt(1, userId);
        psOrder.setString(2, customerName);
        psOrder.setString(3, customerAddress);
        psOrder.setString(4, creditCardNumber);
        psOrder.setTimestamp(5, purchaseDate);  // Purchase date
        psOrder.setTimestamp(6, shipDate);  // Ship date
        psOrder.setTimestamp(7, deliveryDate);  // Delivery date
        psOrder.setDouble(8, totalSales);
        psOrder.setString(9, deliveryType);

        if (storeId != 0) {
            psOrder.setInt(10, storeId);
            psOrder.setString(11, storeAddress);
        }

        psOrder.executeUpdate();

        // Get the generated order ID
        ResultSet rs = psOrder.getGeneratedKeys();
        int orderId = 0;
        if (rs.next()) {
            orderId = rs.getInt(1);
        }
        rs.close();
        psOrder.close();

        // Insert order items into Order_Items table
        for (String productName : userCart.keySet()) {
            Cart product = userCart.get(productName);

            // Fetch product details (id, category, discount) from the Products table using productName
            String productSQL = "SELECT id, category, discount FROM Products WHERE name = ?";
            PreparedStatement psProduct = conn.prepareStatement(productSQL);
            psProduct.setString(1, productName);  // Use product name as the identifier
            ResultSet rsProduct = psProduct.executeQuery();

            if (rsProduct.next()) {
                int productId = rsProduct.getInt("id");  // Get the actual product ID
                String category = rsProduct.getString("category");
                double discount = rsProduct.getDouble("discount");

                // Insert the order item into Order_Items table
                String insertOrderItemSQL = "INSERT INTO Order_Items (order_id, product_id, category, quantity, price, discount,product_name) VALUES (?, ?, ?, ?, ?, ?,?)";
                PreparedStatement psItem = conn.prepareStatement(insertOrderItemSQL);
                psItem.setInt(1, orderId);  // Use the generated order ID
                psItem.setInt(2, productId);  // Insert the correct product ID
                psItem.setString(3, category);  // Category fetched from Products table
                psItem.setInt(4, product.getQuantity());  // Product quantity from cart
                psItem.setDouble(5, product.getProductPrice());  // Price from cart
                psItem.setDouble(6, discount);  // Discount from Products table
                psItem.setString(7, productName);
                psItem.executeUpdate();
                
                psItem.close();
                updateProductQuantity(productId, product.getQuantity());  //
            }

            // Close resources
            rsProduct.close();
            psProduct.close();
        }

        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


// Method to reduce product quantity
public boolean updateProductQuantity(int productId, int quantityOrdered) {
    String updateQuantitySQL = "UPDATE products SET quantity = quantity - ? WHERE id = ?";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(updateQuantitySQL)) {
        ps.setInt(1, quantityOrdered);
        ps.setInt(2, productId);
        ps.executeUpdate();
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

public int getUserIdByName(String username) throws SQLException {
    try (Connection conn = getConnection()) {
        // Update the SQL to select from the 'customers' table instead of 'Users'
        String getUserSQL = "SELECT id FROM customers WHERE full_name = ?"; // Or 'email' if that's the key
        PreparedStatement ps = conn.prepareStatement(getUserSQL);
        ps.setString(1, username);
        System.out.println("username : " +  username);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("id");  // Make sure 'customer_id' exists in the 'customers' table
        }
        throw new SQLException("Customer not found.");
    }
}

public int getStoreDetailsByLocation(String storeLocation) throws SQLException {
    Connection conn = getConnection();
    String query = "SELECT store_id FROM Stores WHERE street = ?";
    PreparedStatement ps = conn.prepareStatement(query);
    ps.setString(1, storeLocation);  // Assuming storeLocation is the street name
    
    ResultSet rs = ps.executeQuery();
    int storeId = -1; // Initialize with -1 to indicate not found
    
    if (rs.next()) {
        storeId = rs.getInt("store_id");  // Retrieve the store_id from the result set
    }

    rs.close();
    ps.close();
    conn.close();

    return storeId;
}

public static boolean deleteOrderFromDatabase(int orderId) {
    String deleteOrderSQL = "DELETE FROM Orders WHERE order_id = ?";
        String deleteOrderItemsSQL = "DELETE FROM Order_Items WHERE order_id = ?";  // Make sure to delete associated order items first

        try (Connection conn = MySQLDataStoreUtilities.getConnection();
             PreparedStatement psOrderItems = conn.prepareStatement(deleteOrderItemsSQL);
             PreparedStatement psOrder = conn.prepareStatement(deleteOrderSQL)) {

            // Delete from Order_Items table first
            psOrderItems.setInt(1, orderId);
            psOrderItems.executeUpdate();

            // Delete from Orders table
            psOrder.setInt(1, orderId);
            int rowsAffected = psOrder.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }}

// Inside MySQLDataStoreUtilities.java

public List<Map<String, Object>> getAllProductsWithQuantity() throws SQLException {
    List<Map<String, Object>> products = new ArrayList<>();
    String query = "SELECT name, price, quantity FROM products";

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(query);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Map<String, Object> product = new HashMap<>();
            product.put("name", rs.getString("name"));
            product.put("price", rs.getDouble("price"));
            product.put("quantity", rs.getInt("quantity"));
            products.add(product);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        throw new SQLException("Error fetching product data.");
    }

    return products;
}

public List<Product> getAllProductsWithQuantityChart() {
    List<Product> products = new ArrayList<>();
    String query = "SELECT * FROM products";

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(query);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Product product = new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getString("category"),
                rs.getString("description"),
                rs.getString("image_path"),
                rs.getDouble("discount"),
                rs.getInt("warranty"),
                rs.getDouble("manufacturer_rebate"),
                rs.getInt("quantity")  // Fetch quantity from DB
            );
            products.add(product);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return products;
}

public List<Product> getProductsOnSale() {
    List<Product> products = new ArrayList<>();
    String query = "SELECT * FROM products WHERE discount > 0"; // Fetch products with a discount

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(query);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Product product = new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getString("category"),
                rs.getString("description"),
                rs.getString("image_path"),
                rs.getDouble("discount"),
                rs.getInt("warranty"),
                rs.getDouble("manufacturer_rebate"),
                rs.getInt("quantity")
            );
            products.add(product);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return products;
}

public List<Product> getProductsWithRebate() {
    List<Product> products = new ArrayList<>();
    String query = "SELECT * FROM products WHERE manufacturer_rebate > 0"; // Fetch products with a rebate

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(query);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Product product = new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getString("category"),
                rs.getString("description"),
                rs.getString("image_path"),
                rs.getDouble("discount"),
                rs.getInt("warranty"),
                rs.getDouble("manufacturer_rebate"),
                rs.getInt("quantity")
            );
            products.add(product);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return products;
}


public List<Product> searchProductsByName(String searchQuery) {
    List<Product> products = new ArrayList<>();
    String query = "SELECT * FROM products WHERE name LIKE ? LIMIT 10";  // Limit results for auto-complete

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {

        ps.setString(1, "%" + searchQuery + "%");  // Allow partial matching
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Product product = new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getString("category"),
                rs.getString("description"),
                rs.getString("image_path"),
                rs.getDouble("discount"),
                rs.getInt("warranty"),
                rs.getDouble("manufacturer_rebate"),
                rs.getInt("quantity")
            );
            products.add(product);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return products;
}

public static boolean isProductsTableEmpty() throws Exception {
    try (Connection conn = getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM products")) {
        if (rs.next()) {
            return rs.getInt(1) == 0;
        }
    }
    return false;
}

// Get all sold products and their sales data
public static List<Product> getAllProductsSold() throws SQLException {
    List<Product> soldProducts = new ArrayList<>();
    String query = "SELECT p.name, p.price, SUM(oi.quantity) AS total_quantity, SUM(oi.price * oi.quantity) AS total_sales " +
                   "FROM products p " +
                   "JOIN order_items oi ON p.id = oi.product_id " +
                   "GROUP BY p.name, p.price";

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(query);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            Product product = new Product();
            product.setName(rs.getString("name"));
            product.setPrice(rs.getDouble("price"));
            product.setQuantitySold(rs.getInt("total_quantity"));
            product.setTotalSales(rs.getDouble("total_sales"));
            soldProducts.add(product);
        }
    }
    return soldProducts;
}

// Get daily sales transactions
public static Map<String, Double> getDailySalesTransactions() throws SQLException {
    Map<String, Double> dailySales = new HashMap<>();
    String query = "SELECT DATE(purchase_date) AS sale_date, SUM(total_sales) AS total_sales " +
                   "FROM orders " +
                   "GROUP BY sale_date";

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(query);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            String saleDate = rs.getString("sale_date");
            double totalSales = rs.getDouble("total_sales");
            dailySales.put(saleDate, totalSales);
        }
    }
    return dailySales;
}

public static void saveTicket(String orderId, String username, String query, String base64Image, String response) {
    String insertSQL = "INSERT INTO tickets (order_id, username, query, image, response) VALUES (?, ?, ?, ?, ?)";
    
    try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
        
        preparedStatement.setString(1, orderId);
        preparedStatement.setString(2, username);
        preparedStatement.setString(3, query);
        preparedStatement.setBytes(4, Base64.getDecoder().decode(base64Image)); // Store image as binary
        preparedStatement.setString(5, response);
        
        preparedStatement.executeUpdate();
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


public static Ticket getTicketById(String ticketId) {
    Ticket ticket = null;

    String query = "SELECT id, order_id, username, query, image, response, created_at FROM tickets WHERE id = ?";
    
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {

        ps.setString(1, ticketId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            ticket = new Ticket();
            ticket.setId(rs.getInt("id"));
            ticket.setOrderId(rs.getInt("order_id"));
            ticket.setUsername(rs.getString("username"));
            ticket.setQuery(rs.getString("query"));
            ticket.setImage(rs.getBytes("image")); // Assuming image is stored as a BLOB
            ticket.setResponse(rs.getString("response"));
            ticket.setCreatedAt(rs.getTimestamp("created_at"));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return ticket;
}

}