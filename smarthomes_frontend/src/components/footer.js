import React from 'react';
import './footer.css'; // Ensure you're importing the correct CSS for the footer

const Footer = () => {
  return (
    <div className="footer-container">
      <div className="footer-row">
        <div className="footer-column social-media">
          <ul>
            <li><a href="#"><i className="fab fa-facebook-f"></i></a></li>
            <li><a href="#"><i className="fab fa-twitter"></i></a></li>
            <li><a href="#"><i className="fab fa-youtube"></i></a></li>
            <li><a href="#"><i className="fab fa-instagram"></i></a></li>
          </ul>
        </div>

        <div className="footer-column">
          <h3>Shop</h3>
          <ul>
            <li><a href="#">Smart Doorbells</a></li>
            <li><a href="#">Smart Doorlocks</a></li>
            <li><a href="#">Smart Thermostats</a></li>
            <li><a href="#">Smart Lights</a></li>
            <li><a href="#">Accessories</a></li>
          </ul>
        </div>

        <div className="footer-column">
          <h3>Store Policy</h3>
          <ul>
            <li><a href="#">Shipping & Returns</a></li>
            <li><a href="#">Store Policy</a></li>
            <li><a href="#">Payment Methods</a></li>
            <li><a href="#">FAQ</a></li>
          </ul>
        </div>

        <div className="footer-column">
          <h3>Opening Hours</h3>
          <p>Mon - Fri: 7am - 10pm</p>
          <p>Saturday: 8am - 10pm</p>
          <p>Sunday: 8am - 11pm</p>
        </div>

        <div className="footer-column">
          <h3>Come Visit</h3>
          <p>2951 S King Drive</p>
          <p>Chicago, IL</p>
          <p>contac@shomes.com</p>
          <p>Tel: 7739494319</p>
        </div>
      </div>

      <div className="footer-bottom">
        <p>&copy; 2024 by SmartHomes.</p>
      </div>
    </div>
  );
};

export default Footer;
