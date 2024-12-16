import React, { useState, useEffect } from 'react';
import './productcategories.css';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const ProductCategories = ({ username }) => {
  const [products, setProducts] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('All Products');
  const [quantity, setQuantity] = useState({});
  const [notification, setNotification] = useState('');
  const navigate = useNavigate();

  // Define categories array
  const categories = [
    { name: 'All Products', folder: 'all' },
    { name: 'Smart Doorbells', folder: 'smartdoorbells' },
    { name: 'Smart Doorlocks', folder: 'smartdoorlocks' },
    { name: 'Smart Speakers', folder: 'smartspeakers' },
    { name: 'Smart Lightings', folder: 'smartbulbs' },
    { name: 'Smart Thermostats', folder: 'smartthermostats' }
  ];

  // Array of random image names
  const randomImages = [
    'AuraBrightRGB',
    'CyberShield360',
    'VisionPro5000',
    'RingGuardX1',
    'SlimSecure300',
    'UltraViewCylindro',
    'HomeSafeElite',
    'SecureLockPro',
    'TitanGuardTouch',
    'NeoSecureX1',
    'OptiLockInfinity',
    'EchoBlast360',
    'SoundWaveAura',
    'CubeSoundMini',
    'PulseBeatVertical',
    'SonicCoreNeo',
    'LumiGlowPro',
    'FlexiLightStrip',
    'HaloSmartCeiling',
    'EdgeGlowWall',
    'ClimaGuardX1',
    'TempSensePro',
    'OvalAirElite',
    'EcoControlS2'
  ];

  // Randomly assign an image to each product
  const assignRandomImages = (products) => {
    return products.map((product) => {
      const randomImage =
        randomImages[Math.floor(Math.random() * randomImages.length)];
      return { ...product, randomImage };
    });
  };

  // Fetch products based on the selected category
  const fetchProducts = async (category) => {
    try {
      const url =
        category === 'All Products'
          ? 'http://localhost:8000/smarthomes_backend/products'
          : `http://localhost:8000/smarthomes_backend/products?category=${encodeURIComponent(
              category
            )}`;

      const response = await axios.get(url);
      const productsWithImages = assignRandomImages(response.data);
      setProducts(productsWithImages);
    } catch (error) {
      console.error('Error fetching product data:', error);
    }
  };

  useEffect(() => {
    // eslint-disable-next-line react-hooks/exhaustive-deps
    fetchProducts(selectedCategory); // Disable eslint warning for this line
  }, [selectedCategory]);

  // Handle category change
  const handleCategoryChange = (category) => {
    setSelectedCategory(category.name);
  };

  // Handle quantity change for each product
  const handleQuantityChange = (product, change) => {
    const newQuantity = { ...quantity };
    newQuantity[product.name] = (newQuantity[product.name] || 0) + change;
    if (newQuantity[product.name] < 1) newQuantity[product.name] = 1;
    setQuantity(newQuantity);
  };

  // Handle adding product to cart
  const handleAddToCart = async (product) => {
    try {
      const selectedQuantity = quantity[product.name] || 1;
      await axios.post('http://localhost:8000/smarthomes_backend/cart', {
        username,
        productName: product.name,
        productPrice: product.price,
        quantity: selectedQuantity
      });
      showNotification('Cart updated successfully!');
    } catch (error) {
      console.error('Error adding product to cart:', error);
    }
  };

  // Function to show notification and hide it after 3 seconds
  const showNotification = (message) => {
    setNotification(message);
    setTimeout(() => {
      setNotification('');
    }, 3000);
  };

  // Navigate to Product Details Page
  const handleViewProduct = (productId) => {
    navigate(`/product/${productId}?username=${username}`);
  };

  return (
    <div className="categories-wrapper">
      {notification && (
        <div className="notification-banner">
          <p>{notification}</p>
        </div>
      )}

      <div className="categories-container">
        <div className="categories-header">
          {categories.map((category, index) => (
            <span
              key={index}
              className={`category-item ${
                selectedCategory === category.name ? 'active' : ''
              }`}
              onClick={() => handleCategoryChange(category)}
            >
              {category.name}
            </span>
          ))}
        </div>

        <div className="products-grid">
          {products.map((product, index) => (
            <div key={index} className="product-card">
              <img
                src={`/images/all/${product.randomImage}.jpeg`}
                alt={product.name}
                className="product-image"
              />
              <div className="product-info">
                <h3>{product.name}</h3>
                <p>Price: ${product.price}</p>

                <div className="quantity-controls">
                  <button onClick={() => handleQuantityChange(product, -1)}>
                    -
                  </button>
                  <span>{quantity[product.name] || 1}</span>
                  <button onClick={() => handleQuantityChange(product, 1)}>
                    +
                  </button>
                </div>
                <button
                  className="add-to-cart-button"
                  onClick={() => handleAddToCart(product)}
                >
                  <i className="fas fa-cart-plus"></i> Add to Cart
                </button>
                <button
                  className="view-product-button"
                  onClick={() => handleViewProduct(product.id)}
                >
                  View Product
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default ProductCategories;