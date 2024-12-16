package com.smarthomes.models;

import java.util.Collection;

import java.io.Serializable;

public class Product implements Serializable {
    private int id;
    private String name;
    private double price;
    private String category;
    private String description;
    private String image_path;
    private double discount; // New field for discount
    private int warranty; // New field for warranty (in years)
    private double manufacturerRebate; // New field for manufacturer rebate
    private int quantity; // New field for available quantity
    private int quantitySold; // New field for the quantity sold
    private double totalSales; // New field for total sales


    public Product(){

    }

    public Product(int id, String name, double price, String category, String description, String image_path) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.image_path = image_path;
        
    }

    // Constructor with all fields including discount, warranty, and rebate
    public Product(int id, String name, double price, String category, String description, String image_path, double discount, int warranty, double manufacturerRebate, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.image_path = image_path;
        this.discount = discount;
        this.warranty = warranty;
        this.manufacturerRebate = manufacturerRebate;
        this.quantity = quantity; // Initialize quantity
    }

    // Constructor without image path but with quantity, discount, warranty, and rebate
    public Product(int id, String name, double price, String category, String description, double discount, int warranty, double manufacturerRebate, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.discount = discount;
        this.warranty = warranty;
        this.manufacturerRebate = manufacturerRebate;
        this.quantity = quantity; // Initialize quantity
    }


    public Product(int id, String name, double price, String category, String description, double discount, int warranty, double manufacturerRebate, int quantity, int quantitySold, double totalSales) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.discount = discount;
        this.warranty = warranty;
        this.manufacturerRebate = manufacturerRebate;
        this.quantity = quantity;
        this.quantitySold = quantitySold;
        this.totalSales = totalSales;
    }

    
    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

    public double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(double totalSales) {
        this.totalSales = totalSales;
    }


    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public int getWarranty() {
        return warranty;
    }

    public void setWarranty(int warranty) {
        this.warranty = warranty;
    }

    public double getManufacturerRebate() {
        return manufacturerRebate;
    }

    public void setManufacturerRebate(double manufacturerRebate) {
        this.manufacturerRebate = manufacturerRebate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Getters and Setters for existing fields
    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}