import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import './App.css'; 
import Login from './components/login';
import Home from './components/home';  // We will create this next
import Register from './components/register';  // Another page for new customers to register
import Cart from './components/cart';  // Page for displaying the cart
import Checkout from './components/checkout';  // Page for checkout
import Order from './components/orders';  // Page for viewing orders
import Manager from './components/manager';
import Salesperson from './components/salesperson';
import ManagerNavbar from './components/managernavbar';
import ProductDetail from './components/productdetail';
import Trending from './components/trending';
import Review from './components/review';
import AllProductsInventory from './components/AllProductsInventory';
import OnSaleProducts from './components/OnSaleProducts';
import ManufacturerRebates from './components/ManufacturerRebates';
import ProductBarChart from './components/ProductBarChart';
import AllProductsSold from './components/AllProductsSold'; // Import the AllProductsSold component
import SalesBarChart from './components/SalesBarChart'; // Import the SalesBarChart component
import DailySalesTransactions from './components/DailySalesTransactions'; // Import the DailySalesTransactions component
import CustomerService from './components/CustomerService';


function App() {
  return (
    <Router>
      <Routes>
      <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/home" element={<Home />} />
        <Route path="/register" element={<Register />} />
        <Route path="/cart" element={<Cart />} />
        <Route path="checkout" element={<Checkout />} />
        <Route path="/vieworders" element={<Order />} />
        <Route path="/product/:productId" element={<ProductDetail />} />
        <Route path="/manager" element={<Manager />} />
        <Route path="/managernavbar" element={<ManagerNavbar/>}/>
        <Route path="/salesperson" element={<Salesperson />} />
        
        <Route path="/review" element={<Review />} />
        <Route path="/trending" element={<Trending />} />
        <Route path="/inventory/all" element={<AllProductsInventory />} />
        <Route path="/inventory/onSale" element={<OnSaleProducts />} />
        <Route path="/inventory/rebate" element={<ManufacturerRebates />} />
        <Route path="/inventory/barChart" element={<ProductBarChart />} />
        <Route path="/salesreport/allProductsSold" element={<AllProductsSold />} />
          <Route path="/salesreport/salesBarChart" element={<SalesBarChart />} />
          <Route path="/salesreport/dailyTransactions" element={<DailySalesTransactions />} />
          <Route path="/customer-service" element={<CustomerService />} />
        
  
      </Routes>
    </Router>
  );
}

export default App;
