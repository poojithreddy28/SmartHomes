import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './inventory.css'; // Create this file for inventory-specific styles

const AllProductsInventory = () => {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    const fetchInventory = async () => {
      try {
        const response = await axios.get('http://localhost:8000/smarthomes_backend/inventory/all');
        setProducts(response.data);  // Set the products data in state
      } catch (error) {
        console.error('Error fetching inventory data:', error);
      }
    };
    
    fetchInventory();
  }, []);

  return (
    <div className="inventory-container">
  <h2 className="inventory-title">All Products Inventory</h2>
  <table className="inventory-table">
    <thead>
      <tr>
        <th>Product Name</th>
        <th>Price</th>
        <th>Available Quantity</th>
      </tr>
    </thead>
    <tbody>
      {products.map((product, index) => (
        <tr key={index}>
          <td>{product.name}</td>
          <td className="price">${product.price.toFixed(2)}</td>
          <td className="quantity">{product.quantity}</td>
        </tr>
      ))}
    </tbody>
  </table>
</div>
  );
};

export default AllProductsInventory;