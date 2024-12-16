import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './allProductsSold.css'; // Add your CSS styles if needed

const AllProductsSold = () => {
  const [productsSold, setProductsSold] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchProductsSold = async () => {
      try {
        const response = await axios.get('http://localhost:8000/smarthomes_backend/salesreport/allProductsSold');
        setProductsSold(response.data.products);
        setLoading(false);
      } catch (err) {
        setError('Error fetching products sold data.');
        setLoading(false);
      }
    };

    fetchProductsSold();
  }, []);

  if (loading) return <div>Loading data...</div>;
  if (error) return <div>{error}</div>;

  return (
    <div className="products-sold-container">
      <h2>All Products Sold</h2>
      <table className="products-sold-table">
        <thead>
          <tr>
            <th>Product Name</th>
            <th>Price</th>
            <th>Quantity Sold</th>
            <th>Total Sales</th>
          </tr>
        </thead>
        <tbody>
          {productsSold.map((product, index) => (
            <tr key={index}>
              <td>{product.name}</td>
              <td>${product.price}</td>
              <td>{product.quantitySold}</td>
              <td>${product.totalSales}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default AllProductsSold;