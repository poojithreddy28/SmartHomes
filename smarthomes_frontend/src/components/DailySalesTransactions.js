import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './dailySalesTransactions.css'; // Add your CSS styles if needed

const DailySalesTransactions = () => {
  const [dailySales, setDailySales] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchDailySales = async () => {
      try {
        const response = await axios.get('http://localhost:8000/smarthomes_backend/salesreport/dailyTransactions');
        setDailySales(response.data.dailySales);
        setLoading(false);
      } catch (err) {
        setError('Error fetching daily sales data.');
        setLoading(false);
      }
    };

    fetchDailySales();
  }, []);

  if (loading) return <div>Loading data...</div>;
  if (error) return <div>{error}</div>;

  return (
    <div className="daily-sales-transactions-container">
      <h2>Daily Sales Transactions</h2>
      <table className="daily-sales-table">
        <thead>
          <tr>
            <th>Date</th>
            <th>Total Sales</th>
          </tr>
        </thead>
        <tbody>
          {dailySales.map((sale, index) => (
            <tr key={index}>
              <td>{sale.date}</td>
              <td>${sale.totalSales}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default DailySalesTransactions;