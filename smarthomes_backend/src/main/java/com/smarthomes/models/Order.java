package com.smarthomes.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.UUID;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    private String orderId;
    private String username;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String cardNumber;
    private String expiry;
    private String cvv;
    private String shippingMethod;
    private String storeLocation;
    private HashMap<String, Cart> products;
    private LocalDate deliveryDate; // New field for delivery date

    // Constructor
    public Order(String username, String firstName, String lastName, String phone, String email, String address,
                 String city, String state, String postalCode, String cardNumber, String expiry, String cvv,
                 String shippingMethod, String storeLocation, HashMap<String, Cart> products) {
        this.orderId = UUID.randomUUID().toString();  // Generate a unique order ID
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.cardNumber = cardNumber;
        this.expiry = expiry;
        this.cvv = cvv;
        this.shippingMethod = shippingMethod;
        this.storeLocation = storeLocation;
        this.products = products;

        // Automatically set delivery date to 14 days from the current date
        this.deliveryDate = LocalDate.now().plus(14, ChronoUnit.DAYS);
    }

    // Getters
    public String getOrderId() {
        return orderId;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getExpiry() {
        return expiry;
    }

    public String getCvv() {
        return cvv;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }

    public String getStoreLocation() {
        return storeLocation;
    }

    public HashMap<String, Cart> getProducts() {
        return products;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }
}
