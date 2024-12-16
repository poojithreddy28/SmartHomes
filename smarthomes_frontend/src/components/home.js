import React, { useState } from 'react';
import './home.css'; // Include styles
import Navbar from './navbar';
import ProductCategories from './productcategories';
import { useLocation, useNavigate } from 'react-router-dom'; // Import useNavigate for navigation
import Footer from './footer';
import axios from 'axios'; // Import axios for API calls

const Home = () => {
  const location = useLocation(); // Get location object
  const navigate = useNavigate(); // For navigation
  const { username } = location.state || {}; // Extract username from location state

  const [searchQuery, setSearchQuery] = useState(''); // State for search reviews input
  const [recommendQuery, setRecommendQuery] = useState(''); // State for recommend product input
  const [reviews, setReviews] = useState([]); // State to store searched reviews
  const [recommendedProducts, setRecommendedProducts] = useState([]); // State for recommended products

  // Function to fetch productId from Flask backend using product name
  const fetchProductId = async (productName) => {
    try {
      const response = await axios.get(`http://localhost:5000/get_product_id`, {
        params: { name: productName },
      });
      return response.data.productId; // Assuming the API returns { productId: <id> }
    } catch (error) {
      console.error(`Error fetching product ID for ${productName}:`, error);
      return null; // Return null if the product ID cannot be fetched
    }
  };

  // Function to handle navigation to ProductDetail for product cards
  const handleProductClick = async (product) => {
    let productId = product.id;
    if (!productId) {
      // If productId is not available, fetch it using the product name
      productId = await fetchProductId(product.name);
    }

    if (productId) {
      navigate(`/product/${productId}?username=${username}`);
    } else {
      console.error('Unable to navigate to ProductDetail. Product ID not found.');
    }
  };

  // Function to handle navigation to ProductDetail for review cards
  const handleReviewClick = async (review) => {
    let productId = review.product_id;
    if (!productId) {
      // If productId is not available in the review, fetch it using the product name
      productId = await fetchProductId(review.product_name);
    }

    if (productId) {
      navigate(`/product/${productId}?username=${username}`);
    } else {
      console.error('Unable to navigate to ProductDetail. Product ID not found.');
    }
  };

  // Function to handle search reviews
  const handleSearchReviews = async () => {
    try {
      const response = await axios.post('http://localhost:5000/search_reviews', {
        query: searchQuery,
      });
      if (response.data && response.data.results) {
        setReviews(response.data.results); // Update reviews state with response
      } else {
        setReviews([]);
        console.error('Invalid response format for reviews:', response.data);
      }
    } catch (error) {
      console.error('Error searching reviews:', error);
    }
  };

  // Function to handle recommend product
  const handleRecommendProduct = async () => {
    try {
      const response = await axios.post('http://localhost:5000/search_products', {
        query: recommendQuery,
      });
      if (response.data && response.data.results) {
        setRecommendedProducts(response.data.results); // Update recommendedProducts state with response
      } else {
        setRecommendedProducts([]);
        console.error('Invalid response format for recommendations:', response.data);
      }
    } catch (error) {
      console.error('Error recommending product:', error);
    }
  };

  return (
    <div className="home-container">
      <Navbar username={username} /> {/* Pass username as prop */}
      <h1>
        Hey, <span className="home-user">{username ? username : 'Guest'}!</span> Welcome to SmartHomes
      </h1>
      <p>Browse through our categories and find the best products!</p>

      {/* Search and Recommend Section */}
      <div className="search-recommend-section">
        {/* Recommend Product Section */}
        <div className="recommend-container">
          <input
            type="text"
            placeholder="Recommend a Product..."
            className="input-box-1"
            value={recommendQuery}
            onChange={(e) => setRecommendQuery(e.target.value)}
          />
          <button className="search-button-1" onClick={handleRecommendProduct}>
            Recommend Product
          </button>
        </div>

        {/* Search Reviews Section */}
        <div className="search-container">
          <input
            type="text"
            placeholder="Search Reviews..."
            className="input-box-1"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <button className="search-button-1" onClick={handleSearchReviews}>
            Search Reviews
          </button>
        </div>
      </div>

      {/* Display Recommended Products */}
      
  {recommendedProducts.length > 0 && (
  <div className="recommendation-container">
    <h2>Recommended Products</h2>
    <div className="product-card-grid">
      {recommendedProducts.slice(0, 5).map((product, index) => (
        <div
          key={index}
          className="product-card"
          onClick={() => handleProductClick(product)} // Navigate to ProductDetail
        >
          <div className="product-details">
            <h3 className="product-name">{product.name || 'Product Name Not Available'}</h3>
            <p className="product-category">Category: {product.category || 'N/A'}</p>
            <p className="product-price">${product.price ? product.price : 'N/A'}</p>
            <p className ='product-category'><strong>Description:</strong> {product.description || 'No Description Available'}</p>
          </div>
        </div>
      ))}
    </div>
  </div>

      )}

      {/* Display Search Reviews Results */}
{reviews.length > 0 && (
  <div className="reviews-container">
    <h2>Matching Reviews</h2>
    <div className="reviews-grid">
      {reviews.slice(0, 5).map((review, index) => (
        <div
          key={index}
          className="review-card"
          onClick={() => handleReviewClick(review)} // Navigate to ProductDetail
        >
          <div className="review-details">
            <p className="review-text">
              <strong>Review:</strong> {review.review_text || 'No Review Text Available'}
            </p>
            <p className="review-rating">
              <strong>Rating:</strong> {review.rating || 'No Rating'}
              <span className="stars">
                {'★'.repeat(review.rating || 0)}{'☆'.repeat(5 - (review.rating || 0))}
              </span>
            </p>
            <p className="review-rated-by">
              <strong>Rated By:</strong> {review.rated_by || 'Anonymous'}
            </p>
            <p className="review-product-name">
              <strong>Product Name:</strong> {review.product_name || 'Unknown Product'}
            </p>
          </div>
        </div>
      ))}
    </div>
  </div>
)}

      <ProductCategories username={username} />
      <Footer />
    </div>
  );
};

export default Home;