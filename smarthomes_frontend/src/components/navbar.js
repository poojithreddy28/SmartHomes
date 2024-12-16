import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './navbar.css';

const Navbar = ({ username }) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [suggestions, setSuggestions] = useState([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const navigate = useNavigate();

  // Handle navigation with username
  const handleNavigation = (path) => {
    navigate(path, { state: { username } });
  };

  // Handle search input changes
  const handleSearchChange = async (e) => {
    const query = e.target.value;
    setSearchQuery(query);

    if (query.length > 0) {
      try {
        const response = await axios.get(
          `http://localhost:8000/smarthomes_backend/products/search?query=${query}`
        );
        setSuggestions(response.data);
        setShowSuggestions(true);
      } catch (error) {
        console.error('Error fetching product suggestions:', error);
      }
    } else {
      setShowSuggestions(false);
    }
  };

  // Handle search suggestion click
  const handleSuggestionClick = (productId) => {
    setSearchQuery(''); // Clear search input
    setShowSuggestions(false); // Hide suggestions
    navigate(`/product/${productId}?username=${username}`); // Navigate to product page
  };

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <div className="navbar-logo" onClick={() => handleNavigation('/')}>
          <p  className= "logo-color"style={{ fontFamily: 'Tiny5, sans-serif' }}> SmartHomes</p>
          {/* <img src={`/images/logo.png`} alt="SmartHomes Logo" className="logo-image" /> */}
        </div>

        <ul className="navbar-menu">
          <li className="navbar-item">
            <span className="navbar-link" onClick={() => handleNavigation('/')}>
              Home
            </span>
          </li>
          
          <li className="navbar-item">
            <span className="navbar-link" onClick={() => handleNavigation('/trending')}>
              Trending 🔥
            </span>
          </li>
          <li className="navbar-item">
            <span className="navbar-link" onClick={() => handleNavigation('/customer-service')}>
              Customer Service
            </span>
          </li>
          {username && (
            <li className="navbar-item">
              <Link to={`/vieworders?username=${username}`} className="navbar-link">
                View Orders
              </Link>
            </li>
          )}
          {username && (
            <Link to={`/cart?username=${username}`} className="navbar-icon">
              <i className="fas fa-shopping-cart"></i>
            </Link>
          )}
        </ul>

        <div className="search-container">
          <input
            type="text"
            className="search-input"
            placeholder="Search for products..."
            value={searchQuery}
            onChange={handleSearchChange}
          />
          {showSuggestions && (
            <ul className="suggestions-list">
              {suggestions.length > 0 ? (
                suggestions.map((product) => (
                  <li
                    key={product.id}
                    onClick={() => handleSuggestionClick(product.id)}
                    className="suggestion-item"
                  >
                    {product.name}
                  </li>
                ))
              ) : (
                <li className="suggestion-item">No matching products found</li>
              )}
            </ul>
          )}
        </div>

        <div className="navbar-icons">
          {username ? (
            <div onClick={() => handleNavigation('/login')} className="navbar-icon logout-button">
              <i className="fas fa-sign-out-alt"> Logout</i>
            </div>
          ) : (
            <span className="navbar-link" onClick={() => handleNavigation('/login')}>
              <i className="fas fa-sign-in-alt"> Login</i>
            </span>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;