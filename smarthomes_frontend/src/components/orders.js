import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './orders.css';
import Navbar from './navbar';

const Order = () => {
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const username = queryParams.get('username'); // Extract username from URL
  const [orders, setOrders] = useState([]);
  const [notification, setNotification] = useState('');
  const navigate = useNavigate();  // useNavigate for routing

  useEffect(() => {
    // Fetch user's orders when the component is mounted
    if (username) {
      axios
        .get(`http://localhost:8000/smarthomes_backend/orders?username=${username}`)
        .then((response) => {
          setOrders(response.data); // Store orders in the state
        })
        .catch((error) => {
          console.error('Error fetching orders:', error);
        });
    }
  }, [username]);

  const calculateTotalPrice = (products) => {
    return Object.keys(products).reduce((total, productName) => {
      return total + products[productName].quantity * products[productName].productPrice;
    }, 0);
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString(); // Format the date to 'MM/DD/YYYY' or local format
  };

  const handleCancelOrder = async (orderId) => {
    try {
      const response = await axios.delete('http://localhost:8000/smarthomes_backend/cancel_order', {
        data: {
          orderId: orderId, // Pass orderId in the body
        },
      });
      if (response.status === 200) {
        setOrders(orders.filter((order) => order.orderId !== orderId));
        showNotification('Order canceled successfully');
      }
    } catch (error) {
      console.error('Error canceling order:', error);
    }
  };

  const showNotification = (message) => {
    setNotification(message);
    setTimeout(() => {
      setNotification(''); // Clear notification after 2 seconds
    }, 2000);
  };

  const handleWriteReview = (order, productId) => {
    const productDetails = order.products[productId];
  
    // Navigate to Review page and pass correct product details
    navigate(`/review`, {
      state: {
        productId, 
        productModelName: productId,  // The actual product name or ID
        productCategory: productDetails.category,  // Correct category
        productPrice: productDetails.productPrice,  // Price
        storeAddress: order.storeAddress || "Home Delivery",  // If home delivery, store address won't exist
        storeZip: order.storeZip || '60616',  // Use fallback if not provided
        storeCity: order.storeCity || 'Chicago',
        storeState: order.storeState || 'IL',
        userId: username,  // Passing username as user ID
      }
    });
  };

  return (
    <div>
      <Navbar username={username} className="fixed-navbar" />
      <div className="order-container">
        <h2>Your Orders</h2>
        
        {orders.length === 0 ? (
          <p>No orders found.</p>
        ) : (
          <div className="order-list">
            {orders.map((order, index) => (
              <div key={index} className="order-card">
                <h3>Order ID: {order.orderId}</h3>
                <h4>Products:</h4>
                <ul className="product-list">
                  {Object.keys(order.products).map((productId) => (
                    <li key={productId} className="product-item">
                      {/* Display product image */}
                      <img
                        src={`/images/All/${productId}.jpeg`}  // Ensure this path exists for your images
                        alt={productId}
                        className="product-image"
                      />
                      {/* Product details */}
                      <p>
                        {productId} - {order.products[productId].quantity} x $
                        {order.products[productId].productPrice.toFixed(2)}
                      </p>
                      {/* Add button to write a review */}
                      <button onClick={() => handleWriteReview(order, productId)}>Write Review</button>
                    </li>
                  ))}
                </ul>

                {/* Display the total price */}
                <p className="total-price">
                  <strong>Total Price: </strong>${calculateTotalPrice(order.products).toFixed(2)}
                </p>

                {/* Display the delivery date */}
                <p><strong>Delivery Date:</strong> {formatDate(order.deliveryDate)}</p>

                {/* Display all order details */}
                <div className="order-details">
                  <p><strong>Name:</strong> {order.customerName}</p>
                  <p><strong>Address:</strong> {order.customerAddress}</p>
                  <p><strong>Shipping Method:</strong> {order.shippingMethod}</p>
                  {order.shippingMethod === 'InStore Pickup' && (
                    <p><strong>Store Location:</strong> {order.storeAddress}</p>
                  )}
                </div>

                {/* Add Cancel Order button */}
                <button className="cancel-order-button" onClick={() => handleCancelOrder(order.orderId)}>
                  Cancel Order
                </button>
              </div>
            ))}
          </div>
        )}
        
        {notification && (
          <div className="notification">
            <p>{notification}</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Order;