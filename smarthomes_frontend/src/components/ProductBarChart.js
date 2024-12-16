import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Chart } from 'react-google-charts';
import './inventory.css';

const ProductBarChart = () => {
  const [chartData, setChartData] = useState([['Product Name', 'Available Quantity']]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchInventoryData = async () => {
      try {
        const response = await axios.get('http://localhost:8000/smarthomes_backend/inventory/chart');
        const products = response.data.products;

        const data = [['Product Name', 'Available Quantity']];
        products.forEach((product) => {
          data.push([product.name, product.quantity]);
        });

        setChartData(data);
        setLoading(false);
      } catch (err) {
        setError('Error fetching product inventory data.');
        setLoading(false);
      }
    };

    fetchInventoryData();
  }, []);

  if (loading) return <div>Loading chart...</div>;
  if (error) return <div>{error}</div>;

  return (
    <div className="inventory-chart-container">
      <h2>Product Inventory Chart</h2>
      <Chart
        chartType="BarChart"
        width="100%"
        height="400px"
        data={chartData}
        options={{
          title: 'Available Quantity of Products',
          chartArea: { width: '60%' },
          hAxis: {
            title: 'Total Quantity',
            minValue: 0,
          },
          vAxis: {
            title: 'Product Name',
          },
        }}
      />
    </div>
  );
};

export default ProductBarChart;