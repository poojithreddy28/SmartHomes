import React, { useState, useEffect } from 'react';
import './manager.css';
import axios from 'axios';
import ManagerNavbar from './managernavbar';
import { useLocation } from 'react-router-dom'; // Import the useLocation hook

const Manager = () => {
  const [products, setProducts] = useState([]);
  const [newProduct, setNewProduct] = useState({
    name: '',
    price: 0,
    category: 'Smart Doorbells',
    description: '',
    image: '',
    discount: '',  // New field for discount
    warranty: '',  // New field for warranty
    manufacturerRebate: '',  // New field for manufacturer rebate
    quantity: ''   // New field for quantity
  });
  const [selectedImage, setSelectedImage] = useState(null); // Store selected image file
  const [selectedCategory, setSelectedCategory] = useState('All Products');
  const [notification, setNotification] = useState('');
  const location = useLocation(); // Get location object
  const { username } = location.state || {}; // Extract username from location state

  const categories = [
    { name: 'All Products' },
    { name: 'Smart Doorbells' },
    { name: 'Smart Doorlocks' },
    { name: 'Smart Speakers' },
    { name: 'Smart Lightings' },
    { name: 'Smart Thermostats' }
  ];

  // Fetch products based on the selected category
  const fetchProducts = async (category) => {
    try {
      const url = category === 'All Products'
        ? 'http://localhost:8000/smarthomes_backend/products'
        : `http://localhost:8000/smarthomes_backend/products?category=${encodeURIComponent(category)}`;
      
      const response = await axios.get(url);
      setProducts(response.data);
    } catch (error) {
      console.error('Error fetching product data:', error);
    }
  };

  useEffect(() => {
    fetchProducts(selectedCategory);
  }, [selectedCategory]);

  // Handle category change
  const handleCategoryChange = (category) => {
    setSelectedCategory(category);
  };

  // Handle image selection and preview
  const handleImageChange = (event) => {
    const file = event.target.files[0];
    if (file) {
      const imageUrl = URL.createObjectURL(file); // For preview
      setSelectedImage(file); // Store the actual file object
      setNewProduct({ ...newProduct, image: file.name }); // Store the filename
    }
  };

  // Add a new product
  const handleAddProduct = async () => {
    try {
      const formData = new FormData();
      formData.append('name', newProduct.name);
      formData.append('price', newProduct.price);
      formData.append('category', newProduct.category);
      formData.append('description', newProduct.description);
      formData.append('image', selectedImage); // Append image file
      formData.append('discount', newProduct.discount); // Append discount
      formData.append('warranty', newProduct.warranty); // Append warranty
      formData.append('manufacturerRebate', newProduct.manufacturerRebate); // Append rebate
      formData.append('quantity', newProduct.quantity); // Append quantity

      const response = await axios.post('http://localhost:8000/smarthomes_backend/products', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });

      showNotification('Product added successfully!');
      fetchProducts(selectedCategory); // Refresh product list
      setNewProduct({ name: '', price: 0, category: 'Smart Doorbells', description: '', image: '', quantity: 0 }); // Reset form
      setSelectedImage(null); // Reset image preview
    } catch (error) {
      console.error('Error adding product:', error);
    }
  };

  // Delete product
  const handleDeleteProduct = async (productId) => {
    try {
      const response = await axios.delete('http://localhost:8000/smarthomes_backend/products', {
        data: { id: productId }  // Ensure the ID is sent
      });
      showNotification('Product deleted successfully!');
      fetchProducts(selectedCategory);
    } catch (error) {
      console.error('Error deleting product:', error);
    }
  };

  // Update product
  const handleUpdateProduct = async (product) => {
    try {
      const response = await axios.put('http://localhost:8000/smarthomes_backend/products', product);
      showNotification('Product updated successfully!');
      fetchProducts(selectedCategory);
    } catch (error) {
      console.error('Error updating product:', error);
    }
  };

  // Function to show notification and hide it after 3 seconds
  const showNotification = (message) => {
    setNotification(message);
    setTimeout(() => {
      setNotification('');
    }, 3000); // Hide after 3 seconds
  };

  return (
    <div className="manager-container">
      {/* Display notification banner */}
      {notification && (
        <div className="notification-banner">
          <p>{notification}</p>
        </div>
      )}

      <ManagerNavbar username={username} /> {/* Pass username as prop */}
      <h1>Welcome, Store Manager</h1>

      {/* Category selection */}
      <div className="categories-header">
        {categories.map((category, index) => (
          <span
            key={index}
            className={`category-item ${selectedCategory === category.name ? 'active' : ''}`}
            onClick={() => handleCategoryChange(category.name)}
          >
            {category.name}
          </span>
        ))}
      </div>

      {/* Add New Product Form */}
      <div className="new-product-form">
        <h2>Add New Product</h2>
        <input
          type="text"
          placeholder="Product Name"
          value={newProduct.name}
          onChange={(e) => setNewProduct({ ...newProduct, name: e.target.value })}
        />
        <input
          type="number"
          placeholder="Price"
          value={newProduct.price}
          onChange={(e) => setNewProduct({ ...newProduct, price: parseFloat(e.target.value) })}
        />
        <select
          value={newProduct.category}
          onChange={(e) => setNewProduct({ ...newProduct, category: e.target.value })}
        >
          {categories.map((category, index) => (
            <option key={index} value={category.name}>{category.name}</option>
          ))}
        </select>
        <textarea
          placeholder="Description"
          value={newProduct.description}
          onChange={(e) => setNewProduct({ ...newProduct, description: e.target.value })}
        />
        {/* Add new inputs for discount, warranty, rebate, and quantity */}
        <input
          type="number"
          placeholder="Discount (%)"
          value={newProduct.discount}
          onChange={(e) => setNewProduct({ ...newProduct, discount: parseFloat(e.target.value) })}
        />
        <input
          type="number"
          placeholder="Warranty (Years)"
          value={newProduct.warranty}
          onChange={(e) => setNewProduct({ ...newProduct, warranty: parseFloat(e.target.value) })}
        />
        <input
          type="number"
          placeholder="Manufacturer Rebate (%)"
          value={newProduct.manufacturerRebate}
          onChange={(e) => setNewProduct({ ...newProduct, manufacturerRebate: parseFloat(e.target.value) })}
        />
        <input
          type="number"
          placeholder="Quantity"
          value={newProduct.quantity}
          onChange={(e) => setNewProduct({ ...newProduct, quantity: parseInt(e.target.value) })}
        />

        {/* Image input */}
        <input
          type="file"
          accept="image/*"
          onChange={handleImageChange}
        />

        {/* Image Preview */}
        {selectedImage && (
          <div className="image-preview">
            <img src={URL.createObjectURL(selectedImage)} alt="Selected product" />
          </div>
        )}

        <button onClick={handleAddProduct}>Add Product</button>
      </div>

      {/* Products Grid */}
      <div className="products-grid">
        {products.map((product, index) => (
          <div key={index} className="product-card">
            <div className="product-info">
              <h3>{product.name}</h3>
              <p>Price: ${product.price}</p>
              <p>Category: {product.category}</p>
              <p>{product.description}</p>
              

              {/* CRUD Buttons for Manager */}
              <button onClick={() => handleUpdateProduct(product)}>Update</button>
              <button onClick={() => handleDeleteProduct(product.id)}>Delete</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Manager;