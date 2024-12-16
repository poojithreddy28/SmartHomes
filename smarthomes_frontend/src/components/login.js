import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';  // Import useNavigate for navigation
import './login.css';  // Import the CSS for styling
import './register.css'; // Reuse register styles if similar
import axios from 'axios';  // Use axios to make HTTP requests

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('Customer'); // Default role to Customer
  const [message, setMessage] = useState('');
  const navigate = useNavigate();  // Hook for navigating to another page

  const handleSubmit = async (event) => {
    event.preventDefault();

    try {
      // Send a POST request to the backend
      const response = await axios.post('http://localhost:8000/smarthomes_backend/login', { email, password, role });

      if (response.status === 200) {
        const username = response.data.user; // Extract username from response
        const role = response.data.role;
        setMessage(response.data.message);

        // Redirect based on role
        if (role === 'Customer') {
          navigate('/home', { state: { username } }); // Pass username to home page
        } else if (role === 'Salesperson') {
          navigate('/salesperson', { state: { username } }); // Pass username to salesperson page
        } else if (role === 'StoreManager') {
          navigate('/manager', { state: { username } }); // Pass username to manager page
        }
      } else {
        setMessage(response.data.message);
      }
    } catch (error) {
      setMessage('Invalid email or password');
      console.error('There was an error!', error);
    }
  };

  return (
    <div className="login-container">
      <div className="login-box">
        <h2>Login</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              type="email"
              id="email"
              placeholder="Enter your email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="role">Select Role</label>
            <select
              id="role"
              value={role}
              onChange={(e) => setRole(e.target.value)}
              required
            >
              <option value="Customer">Customer</option>
              <option value="Salesperson">Salesperson</option>
              <option value="StoreManager">StoreManager</option>
            </select>
          </div>

          <button type="submit" className="login-button">Login</button>
        </form>

        {message && <p className="message">{message}</p>}

        <p className="register-text">
          Don't have an account? <Link to="/register">Sign up here</Link>
        </p>
      </div>
    </div>
  );
};

export default Login;