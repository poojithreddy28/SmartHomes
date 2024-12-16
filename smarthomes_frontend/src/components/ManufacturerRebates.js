import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './inventory.css';

const ManufacturerRebates = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
  
    useEffect(() => {
      const fetchProductsWithRebate = async () => {
        try {
          const response = await axios.get('http://localhost:8000/smarthomes_backend/inventory/rebate');
          setProducts(response.data);
          setLoading(false);
        } catch (error) {
          setError('Error fetching products with rebates.');
          setLoading(false);
        }
      };
  
      fetchProductsWithRebate();
    }, []);
  
    if (loading) return <div>Loading...</div>;
    if (error) return <div>{error}</div>;
  
    return (
      <div className="products-with-rebate">
        <h2>Products with Manufacturer Rebates</h2>
        <table className="inventory-table">
          <thead>
            <tr>
              <th>Product Name</th>
              <th>Price</th>
              <th>Manufacturer Rebate (%)</th>
              <th>Quantity Available</th>
            </tr>
          </thead>
          <tbody>
            {products.map((product, index) => (
              <tr key={index}>
                <td>{product.name}</td>
                <td>${product.price.toFixed(2)}</td>
                <td>{product.manufacturerRebate}</td>
                <td>{product.quantity}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    );
};

export default ManufacturerRebates;