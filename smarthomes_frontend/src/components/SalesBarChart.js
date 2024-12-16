import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Chart } from 'react-google-charts';
import './SalesBarChart.css'

const SalesBarChart = () => {
  const [chartData, setChartData] = useState([['Product Name', 'Total Sales']]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchSalesData = async () => {
      try {
        const response = await axios.get('http://localhost:8000/smarthomes_backend/salesreport/salesBarChart');
        const fetchedData = response.data.chartData; // Use the `chartData` field directly

        // Include the chart headers ('Product Name', 'Total Sales')
        const data = [['Product Name', 'Total Sales'], ...fetchedData];

        setChartData(data);
        setLoading(false);
      } catch (err) {
        setError('Error fetching sales data.');
        setLoading(false);
      }
    };

    fetchSalesData();
  }, []);

  if (loading) return <div>Loading chart...</div>;
  if (error) return <div>{error}</div>;

  return (
    <div className="sales-bar-chart-container">
      <h2>Total Sales for Each Product</h2>
      <Chart
        chartType="BarChart"
        width="100%"
        height="400px"
        data={chartData}
        options={{
          title: 'Total Sales by Product',
          chartArea: { width: '60%' },
          hAxis: {
            title: 'Total Sales ($)',
            minValue: 0,
          },
          vAxis: {
            title: 'Product Name',
          },
          bars: 'horizontal', // Display bars horizontally
          colors: ['#1b9e77'], // Optional: Set color for bars
          legend: { position: 'none' }, // Hide legend if not needed
        }}
      />
    </div>
  );
};

export default SalesBarChart;