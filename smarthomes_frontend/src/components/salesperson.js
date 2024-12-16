import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './salesperson.css'; // Import CSS for styling

const Salesperson = () => {
  const [orders, setOrders] = useState([]);
  const [message, setMessage] = useState('');
  const [editingOrderId, setEditingOrderId] = useState(null);
  const [updatedStatus, setUpdatedStatus] = useState('');

  useEffect(() => {
    fetchOrders();
  }, []);

  // Fetch all orders from the backend
  const fetchOrders = async () => {
    try {
      const response = await axios.get('http://localhost:8000/smarthomes_backend/manage_orders');
      console.log(response.data);
      setOrders(response.data); // Set the orders from the backend
    } catch (error) {
      console.error('Error fetching orders:', error);
    }
  };

  // Function to handle deleting an order
  const handleDeleteOrder = async (orderId) => {
    try {
      const response = await axios.delete(`http://localhost:8000/smarthomes_backend/manage_orders?orderId=${orderId}`);  // Pass orderId as query parameter
      if (response.status === 200) {
        setMessage('Order deleted successfully!');
        setOrders(orders.filter((order) => order.orderId !== orderId)); // Remove the deleted order
        setTimeout(() => setMessage(''), 3000); // Clear message after 3 seconds
      }
    } catch (error) {
      console.error('Error deleting order:', error);
    }
  };

  // Function to handle updating the order status
  const handleUpdateOrder = async (orderId) => {
    try {
      const response = await axios.put('http://localhost:8000/smarthomes_backend/manage_orders', {
        orderId: orderId,
        status: updatedStatus,  // The new status to be updated
      });

      if (response.status === 200) {
        setMessage('Order status updated successfully!');
        // Update the order in the list with the new status
        setOrders(orders.map(order => 
          order.orderId === orderId ? { ...order, status: updatedStatus } : order
        ));
        setEditingOrderId(null); // Exit editing mode
        setTimeout(() => setMessage(''), 3000); // Clear message after 3 seconds
      }
    } catch (error) {
      console.error('Error updating order:', error);
    }
  };

  return (
    <div className="salesperson-container">
      <h1>Salesperson Dashboard</h1>

      {message && <div className="message">{message}</div>}

      <h2>All Orders</h2>
      <ul className="order-list">
        {orders.map((order) => (
          <li key={order.orderId}>
            <p>Order ID: {order.orderId}</p>
            <p>Customer: {order.username}</p>
            <p>Delivery Date: {order.deliveryDate}</p>
            <p>Status: {editingOrderId === order.orderId ? (
              <input 
                type="text"
                value={updatedStatus}
                onChange={(e) => setUpdatedStatus(e.target.value)}
                placeholder={order.status}
              />
            ) : (
              order.status
            )}</p>

            {editingOrderId === order.orderId ? (
              <div>
                <button onClick={() => handleUpdateOrder(order.orderId)}>Save</button>
                <button onClick={() => setEditingOrderId(null)}>Cancel</button>
              </div>
            ) : (
              <div>
                <button onClick={() => setEditingOrderId(order.orderId)}>Edit Status</button>
                <button onClick={() => handleDeleteOrder(order.orderId)}>Delete</button>
              </div>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Salesperson;