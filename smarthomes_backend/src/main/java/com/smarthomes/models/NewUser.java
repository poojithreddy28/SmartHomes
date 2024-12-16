
package com.smarthomes.models;

import java.io.Serializable;

public class NewUser implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fullName;
    private String email;
    private String password;
    private String role;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    
    public NewUser(String fullName, String email, String password, String role, String street, String city, String state, String zipCode) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
