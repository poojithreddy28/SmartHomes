import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
import Navbar from './navbar';
import './productdetail.css';

const ProductDetail = () => {
  const { productId } = useParams();
  const navigate = useNavigate();
  const location = useLocation();

  const queryParams = new URLSearchParams(location.search);
  const username = queryParams.get('username');

  const [product, setProduct] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [likes, setLikes] = useState(0);
  const [isLiked, setIsLiked] = useState(false);
  const [quantity, setQuantity] = useState(1);

  useEffect(() => {
    const fetchProductDetails = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8000/smarthomes_backend/product?productId=${productId}`
        );
        setProduct(response.data);

        const reviewsResponse = await axios.get(
          `http://localhost:8000/smarthomes_backend/get_reviews?productModelName=${response.data.name}`
        );
        setReviews(reviewsResponse.data);

        const storedLikes = localStorage.getItem(`likes-${productId}`);
        const storedIsLiked = localStorage.getItem(`isLiked-${productId}`);

        if (storedLikes) {
          setLikes(parseInt(storedLikes, 10));
        }

        if (storedIsLiked === 'true') {
          setIsLiked(true);
        }
      } catch (error) {
        console.error('Error fetching product details:', error);
      }
    };
    fetchProductDetails();
  }, [productId]);

  const handleLike = () => {
    if (!isLiked) {
      const newLikes = likes + 1;
      setLikes(newLikes);
      setIsLiked(true);

      localStorage.setItem(`likes-${productId}`, newLikes);
      localStorage.setItem(`isLiked-${productId}`, true);
    }
  };

  const handleQuantityChange = (change) => {
    setQuantity((prevQuantity) => Math.max(prevQuantity + change, 1));
  };

  const handleAddToCart = async () => {
    try {
      await axios.post('http://localhost:8000/smarthomes_backend/cart', {
        username,
        productName: product.name,
        productPrice: product.price,
        quantity
      });
      alert('Product added to cart!');
    } catch (error) {
      console.error('Error adding product to cart:', error);
    }
  };

  const handleBackClick = () => {
    navigate('/home', { state: { username } });
  };

  const imagePath = `/images/all/${product?.name?.replace(/\s+/g, '_')}.jpeg`;

  if (!product) return <div>Loading...</div>;

  return (
    <>
      <Navbar username={username} className="fixed-navbar" />

      <div className="scrollable-content">
        <div className="product-detail-container">
          {/* Check if image is available */}
          {product && (
            <div className="product-detail-info">
              <h1>{product.name}</h1>
              <p><strong>Category:</strong> {product.category}</p>
              <p><strong>Description:</strong> {product.description}</p>
              <p><strong>Price:</strong> ${product.price.toFixed(2)}</p>

              {/* Quantity and Add to Cart */}
              <div className="cart-section">
                <div className="quantity-controls">
                  <button onClick={() => handleQuantityChange(-1)}>-</button>
                  <span>{quantity}</span>
                  <button onClick={() => handleQuantityChange(1)}>+</button>
                </div>
                <button className="add-to-cart-button" onClick={handleAddToCart}>
                  Add to Cart
                </button>
              </div>

              {/* Like Button */}
              <div className="like-section">
                <button
                  className={`like-button ${isLiked ? 'liked' : ''}`}
                  onClick={handleLike}
                  disabled={isLiked}
                >
                  <i className={`fas fa-heart ${isLiked ? 'liked' : ''}`}></i> {likes} Likes
                </button>
              </div>
            </div>
          )}

          <button className="back-button" onClick={handleBackClick}>
            Back to Home
          </button>
        </div>

        {/* Reviews Section */}
        <div className="product-reviews">
          <h2>Customer Reviews</h2>
          {reviews.length > 0 ? (
            reviews.map((review, index) => (
              <div key={index} className="review-card">
                <h3>Review by {review.userId}</h3>
                <p><strong>Age:</strong> {review.userAge}</p>
                <p><strong>Occupation:</strong> {review.userOccupation}</p>
                <p><strong>Rating:</strong> {review.reviewRating} / 5</p>
                <p><strong>Review Date:</strong> {new Date(review.reviewDate).toLocaleDateString()}</p>
                <p><strong>Review Text:</strong> {review.reviewText}</p>
                <hr />
              </div>
            ))
          ) : (
            <p>No reviews yet for this product.</p>
          )}
        </div>
      </div>
    </>
  );
};

export default ProductDetail;