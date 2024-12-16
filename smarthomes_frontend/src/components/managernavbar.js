import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './navbar.css';

const ManagerNavbar = ({ username }) => {
  const navigate = useNavigate();
  const [isInventoryOpen, setIsInventoryOpen] = useState(false);
  const [isSalesReportOpen, setIsSalesReportOpen] = useState(false);

  const handleLogout = () => {
    navigate('/login');
  };

  const toggleInventoryDropdown = () => {
    setIsInventoryOpen(!isInventoryOpen);
    setIsSalesReportOpen(false); // Close Sales Report dropdown if open
  };

  const toggleSalesReportDropdown = () => {
    setIsSalesReportOpen(!isSalesReportOpen);
    setIsInventoryOpen(false); // Close Inventory dropdown if open
  };

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <div className="navbar-logo">
          <Link to={`/?username=${username}`} className="logo-link">
            SmartHomes
            <img src={`/images/logo.png`} alt="SmartHomes Logo" className="logo-image" />
          </Link>
        </div>

        <ul className="navbar-menu">
          <li className="navbar-item">
            <Link to={`/?username=${username}`} className="navbar-link">
              Home
            </Link>
          </li>
          <li className="navbar-item">
            <Link to="/about" className="navbar-link">
              About Us
            </Link>
          </li>

          <li className={`navbar-item ${isInventoryOpen ? 'active' : ''}`}>
            <span className="navbar-link" onClick={toggleInventoryDropdown}>
              Inventory <i className={`fas ${isInventoryOpen ? 'fa-chevron-up' : 'fa-chevron-down'}`} />
            </span>
            {isInventoryOpen && (
              <ul className="dropdown-menu dropdown-menu-right">
                <li><Link to="/inventory/all" className="dropdown-item">All Products</Link></li>
                <li><Link to="/inventory/onSale" className="dropdown-item">Products on Sale</Link></li>
                <li><Link to="/inventory/rebate" className="dropdown-item">Products on Manufacturer Rebates</Link></li>
                <li><Link to="/inventory/barChart" className="dropdown-item">Product Bar Chart</Link></li>
              </ul>
            )}
          </li>

          <li className={`navbar-item ${isSalesReportOpen ? 'active' : ''}`}>
            <span className="navbar-link" onClick={toggleSalesReportDropdown}>
              Sales Report <i className={`fas ${isSalesReportOpen ? 'fa-chevron-up' : 'fa-chevron-down'}`} />
            </span>
            {isSalesReportOpen && (
              <ul className="dropdown-menu dropdown-menu-right">
                <li><Link to="/salesreport/allProductsSold" className="dropdown-item">All Products Sold</Link></li>
                <li><Link to="/salesreport/dailyTransactions" className="dropdown-item">Daily Sales Transactions</Link></li>
                <li><Link to="/salesreport/salesBarChart" className="dropdown-item">Sales Bar Chart</Link></li>
              </ul>
            )}
          </li>
        </ul>

        <div className="navbar-icons">
          {username ? (
            <div onClick={handleLogout} className="navbar-icon logout-button">
              <i className="fas fa-sign-out-alt"> Logout</i>
            </div>
          ) : (
            <Link to="/login" className="navbar-icon">
              <i className="fas fa-sign-in-alt"> Login</i>
            </Link>
          )}
        </div>
      </div>
    </nav>
  );
};

export default ManagerNavbar;