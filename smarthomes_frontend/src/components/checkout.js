import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import axios from 'axios';
import './checkout.css';
import Navbar from './navbar';

const Checkout = () => {
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const username = queryParams.get('username'); // Get the username from URL

  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    phone: '',
    email: '',
    address: '',
    city: '',
    state: '',
    postalCode: '',
    cardNumber: '',
    expiry: '',
    cvv: '',
    shippingMethod: 'Home Delivery',
    storeLocation: ''
  });

  const [storeLocations, setStoreLocations] = useState([]); // Store locations fetched from the backend
  const [isInStorePickup, setIsInStorePickup] = useState(false); // Flag to determine if 'InStore Pickup' is selected
  const [isOrderPlaced, setIsOrderPlaced] = useState(false); // Disable button after placing order
  const [orderStatus, setOrderStatus] = useState('Place Order'); // Track order status
// Fetch store locations
const fetchStoreLocations = () => {
  axios
    .get("http://localhost:8000/smarthomes_backend/place_order?action=get_store_data")
    .then((response) => {
      console.log("Store Data:", response.data);
      setStoreLocations(response.data); // Assuming setStoreLocations is your state setter
    })
    .catch((error) => {
      console.error("Error fetching store data:", error);
    });
};

// Call this function when the component mounts or when the user selects 'InStore Pickup'
useEffect(() => {
  if (formData.shippingMethod === "InStore Pickup") {
    fetchStoreLocations();
  }
}, [formData.shippingMethod]);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleShippingMethodChange = (e) => {
    const value = e.target.value;
    setFormData({
      ...formData,
      shippingMethod: value,
    });

    setIsInStorePickup(value === 'InStore Pickup'); // Enable or disable address fields based on selection
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    const orderData = {
      username: username, // Include the username
      ...formData, // Include all form fields
    };

    // Sending data to backend using axios
    axios.post('http://localhost:8000/smarthomes_backend/place_order', orderData)
      .then((response) => {
        console.log('Order successful:', response.data);
        setOrderStatus('Order Placed Successfully'); // Change button text to show success
        setIsOrderPlaced(true); // Disable button after order is placed
      })
      .catch((error) => {
        console.error('Error placing order:', error);
        setOrderStatus('Failed to Place Order'); // Update on failure
      });
  };

  return (
    <div className="checkout-container">
      <Navbar username={username} />
      <h2>CheckOut</h2>
      <form onSubmit={handleSubmit} className="checkout-form">
        <div className="form-group">
          <label>First name</label>
          <input
            type="text"
            name="firstName"
            value={formData.firstName}
            onChange={handleChange}
            placeholder="Type here"
            required
          />
        </div>
        <div className="form-group">
          <label>Last name</label>
          <input
            type="text"
            name="lastName"
            value={formData.lastName}
            onChange={handleChange}
            placeholder="Type here"
            required
          />
        </div>
        <div className="form-group">
          <label>Phone</label>
          <input
            type="tel"
            name="phone"
            value={formData.phone}
            onChange={handleChange}
            placeholder="+1"
            required
          />
        </div>
        <div className="form-group">
          <label>Email</label>
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            placeholder="user@gmail.com"
            required
          />
        </div>

        {/* Address fields - disable if 'InStore Pickup' is selected */}
        <fieldset disabled={isInStorePickup}>
          <div className="form-group">
            <label>Address</label>
            <input
              type="text"
              name="address"
              value={formData.address}
              onChange={handleChange}
              placeholder="Type here"
              required={!isInStorePickup}
            />
          </div>
          <div className="form-group">
            <label>City</label>
            <input
              type="text"
              name="city"
              value={formData.city}
              onChange={handleChange}
              placeholder="City"
              required={!isInStorePickup}
            />
          </div>
          <div className="form-group">
            <label>State</label>
            <input
              type="text"
              name="state"
              value={formData.state}
              onChange={handleChange}
              placeholder="State"
              required={!isInStorePickup}
            />
          </div>
          <div className="form-group">
            <label>Postal code</label>
            <input
              type="text"
              name="postalCode"
              value={formData.postalCode}
              onChange={handleChange}
              placeholder="Postal code"
              required={!isInStorePickup}
            />
          </div>
        </fieldset>

        <div className="form-group">
          <label>Card Number</label>
          <input
            type="text"
            name="cardNumber"
            value={formData.cardNumber}
            onChange={handleChange}
            placeholder="1234123412341234"
            required
          />
        </div>
        <div className="form-group">
          <label>Expiry</label>
          <input
            type="text"
            name="expiry"
            value={formData.expiry}
            onChange={handleChange}
            placeholder="MM/YY"
            required
          />
        </div>
        <div className="form-group">
          <label>CVV</label>
          <input
            type="text"
            name="cvv"
            value={formData.cvv}
            onChange={handleChange}
            placeholder="123"
            required
          />
        </div>

        <div className="form-group">
          <label>Shipping Method</label>
          <div>
            <label>
              <input
                type="radio"
                name="shippingMethod"
                value="Home Delivery"
                checked={formData.shippingMethod === 'Home Delivery'}
                onChange={handleShippingMethodChange}
              />
              Home Delivery
            </label>
            <label>
              <input
                type="radio"
                name="shippingMethod"
                value="InStore Pickup"
                checked={formData.shippingMethod === 'InStore Pickup'}
                onChange={handleShippingMethodChange}
              />
              InStore Pickup
            </label>
          </div>
        </div>

        {isInStorePickup && (
          <div className="form-group">
            <label>Select Store Location</label>
            <select
              name="storeLocation"
              value={formData.storeLocation}
              onChange={handleChange}
              required
            >
              <option value="">Select a store location</option>
              {storeLocations.map((store) => (
                <option key={store.store_id} value={store.address}>
                  {store.city} - {store.state}, {store.zip_code}
                </option>
              ))}
            </select>
          </div>
        )}

        <button
          type="submit"
          className="checkout-button"
          disabled={isOrderPlaced}  // Disable the button if the order is placed
        >
          {orderStatus}
        </button>
      </form>
    </div>
  );
};

export default Checkout;