package com.smarthomes.models;

import java.io.Serializable;

public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String productName;  // Name of the product
    private int quantity;        // Quantity of the product in the cart
    private double productPrice; // Price of the product

    // Constructor
    public Cart(String productName, int quantity, double productPrice) {
        this.productName = productName;
        this.quantity = quantity;
        this.productPrice = productPrice;
    }

    // Getters and Setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", productPrice=" + productPrice +
                '}';
    }
}
