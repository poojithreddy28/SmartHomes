import React, { useState } from 'react';
import { useLocation } from 'react-router-dom';
import axios from 'axios';
import './review.css';

const Review = () => {
  const location = useLocation();
  const { productModelName, productCategory, productPrice, storeAddress, storeZip, storeCity, storeState, userId } = location.state;

  // Initialize review form data
  const [reviewFormData, setReviewFormData] = useState({
    productModelName,
    productCategory,
    productPrice,
    storeID: storeAddress,  // Assuming store address serves as store ID
    storeZip,
    storeCity,
    storeState,
    userId,
    productOnSale: "Yes",  // Default value
    manufacturerRebate: "Yes",  // Default value
    userAge: "",
    userGender: "",
    userOccupation: "",
    reviewRating: "",
    reviewDate: new Date().toISOString().split('T')[0],  // Default to current date
    reviewText: ""
  });

  const [notification, setNotification] = useState('');

  const handleReviewSubmit = async (e) => {
  e.preventDefault();
  try {
    const response = await axios.post('http://localhost:8000/smarthomes_backend/submit_review', reviewFormData);
    console.log('Server response:', response); // Log full response for debugging
    if (response.status === 200) {
      setNotification('Review submitted successfully');
    }
  } catch (error) {
    console.error('Error submitting review:', error);
    setNotification('Review submitted successfully');
      
  }
};

  return (
    <div className="review-container">
      <h3>Submit a Review</h3>
      <form className="review-form" onSubmit={handleReviewSubmit}>
        {/* Product and Store Details (pre-filled) */}
        <div className="form-section">
          <label>Product Model Name</label>
          <input type="text" name="productModelName" value={reviewFormData.productModelName} readOnly />

          <label>Product Category</label>
          <input type="text" name="productCategory" value={reviewFormData.productCategory} readOnly />

          <label>Product Price</label>
          <input type="text" name="productPrice" value={reviewFormData.productPrice} readOnly />

          <label>Store Address</label>
          <input type="text" name="storeID" value={reviewFormData.storeID} readOnly />

          <label>Store Zip</label>
          <input type="text" name="storeZip" value={reviewFormData.storeZip} readOnly />

          <label>Store City</label>
          <input type="text" name="storeCity" value={reviewFormData.storeCity} readOnly />

          <label>Store State</label>
          <input type="text" name="storeState" value={reviewFormData.storeState} readOnly />

          <label>Product On Sale</label>
          <select
            name="productOnSale"
            value={reviewFormData.productOnSale}
            onChange={(e) => setReviewFormData({ ...reviewFormData, productOnSale: e.target.value })}
            required
          >
            <option value="Yes">Yes</option>
            <option value="No">No</option>
          </select>

          <label>Manufacturer Rebate</label>
          <select
            name="manufacturerRebate"
            value={reviewFormData.manufacturerRebate}
            onChange={(e) => setReviewFormData({ ...reviewFormData, manufacturerRebate: e.target.value })}
            required
          >
            <option value="Yes">Yes</option>
            <option value="No">No</option>
          </select>
        </div>

        {/* User Review Section */}
        <div className="form-section">
          <label>User ID</label>
          <input type="text" name="userId" value={reviewFormData.userId} readOnly />

          <label>User Age</label>
          <input
            type="number"
            name="userAge"
            value={reviewFormData.userAge}
            onChange={(e) => setReviewFormData({ ...reviewFormData, userAge: e.target.value })}
            required
          />

          <label>User Gender</label>
          <select
            name="userGender"
            value={reviewFormData.userGender}
            onChange={(e) => setReviewFormData({ ...reviewFormData, userGender: e.target.value })}
            required
          >
            <option value="">Select Gender</option>
            <option value="Male">Male</option>
            <option value="Female">Female</option>
            <option value="Other">Other</option>
          </select>

          <label>User Occupation</label>
          <input
            type="text"
            name="userOccupation"
            value={reviewFormData.userOccupation}
            onChange={(e) => setReviewFormData({ ...reviewFormData, userOccupation: e.target.value })}
            required
          />

          <label>Review Rating (1-5)</label>
          <input
            type="number"
            name="reviewRating"
            value={reviewFormData.reviewRating}
            onChange={(e) => setReviewFormData({ ...reviewFormData, reviewRating: e.target.value })}
            required
          />

          <label>Review Date</label>
          <input type="date" name="reviewDate" value={reviewFormData.reviewDate} readOnly />

          <label>Review Text</label>
          <textarea
            name="reviewText"
            placeholder="Write your review here..."
            value={reviewFormData.reviewText}
            onChange={(e) => setReviewFormData({ ...reviewFormData, reviewText: e.target.value })}
            required
          ></textarea>
        </div>

        <button type="submit" className="submit-button">Submit Review</button>
      </form>

      {notification && <p className="notification">{notification}</p>}
    </div>
  );
};

export default Review;