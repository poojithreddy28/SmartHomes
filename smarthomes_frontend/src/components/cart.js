import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import { useLocation, useNavigate } from 'react-router-dom';  // Import useNavigate
import './cart.css';
import Navbar from './navbar';

const Cart = () => {
  const location = useLocation();
  const navigate = useNavigate(); // Initialize navigation

  // Extract username from query params
  const queryParams = new URLSearchParams(location.search);
  const username = queryParams.get('username');

  const [cartItems, setCartItems] = useState([]);
  const [totalPrice, setTotalPrice] = useState(0);

  // Fetch cart items from the backend
  const fetchCartItems = useCallback(async () => {
    if (!username) {
      console.error("No username found in Cart.js");
      return;
    }

    try {
      const response = await axios.get('http://localhost:8000/smarthomes_backend/cart', {
        params: { username },  // Pass username to backend
      });

      setCartItems(response.data);
      const total = response.data.reduce((acc, item) => acc + item.price * item.quantity, 0);
      setTotalPrice(total);
    } catch (error) {
      console.error('Error fetching cart data:', error);
    }
  }, [username]);

  // Delete item from cart
  const deleteCartItem = async (productName) => {
    try {
      const response = await axios.delete('http://localhost:8000/smarthomes_backend/cart', {
        data: {
          username: username,
          productName: productName,
        },
      });
      if (response.status === 200) {
        fetchCartItems(); // Re-fetch the cart items after successful deletion
      }
    } catch (error) {
      console.error('Error deleting cart item:', error);
    }
  };

  useEffect(() => {
    fetchCartItems();
  }, [fetchCartItems]);

  const proceedToCheckout = () => {
    navigate(`/checkout?username=${username}`); // Navigate to checkout page
  };

  return (
    <div className="cart-container">
       <Navbar username={username} />
      <h2>Your Cart</h2>
      {cartItems.length === 0 ? (
        <p>Your cart is empty!</p>
      ) : (
        <div>
          <ul>
            {cartItems.map((item, index) => (
              <li key={index} className="cart-item">
                <img 
                  src={`/images/All/${item.productName}.jpeg`} 
                  alt={item.productName} 
                  className="cart-item-image"
                />
                <div className="cart-item-info">
                  <h3>{item.productName}</h3>
                  <p>${item.price} x {item.quantity}</p>
                </div>
                <button
                  className="delete-button"
                  onClick={() => deleteCartItem(item.productName)}
                >
                  Delete
                </button>
              </li>
            ))}
          </ul>
          <h3 className="cart-total">Total Price: ${totalPrice.toFixed(2)}</h3>
          {/* Proceed to Checkout Button */}
          <button className="checkout-button-cart" onClick={proceedToCheckout}>
            Proceed to Checkout
          </button>
        </div>
      )}
    </div>
  );
};

export default Cart;
