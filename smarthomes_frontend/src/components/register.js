import React, { useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import './register.css';  // Updated CSS filename

const Register = () => {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [role, setRole] = useState('Customer');
  const [street, setStreet] = useState('');
  const [city, setCity] = useState('');
  const [state, setState] = useState('');
  const [zipCode, setZipCode] = useState('');
  const [message, setMessage] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  const handleSignup = async (event) => {
    event.preventDefault();

    if (password !== confirmPassword) {
      setMessage("Passwords do not match");
      return;
    }

    try {
      const response = await axios.post('http://localhost:8000/smarthomes_backend/signup', {
        fullName: name,
        email: email,
        password: password,
        role: role,
        street: street,
        city: city,
        state: state,
        zipCode: zipCode,
      }, {
        headers: {
          'Content-Type': 'application/json',
        }
      });

      if (response.status === 201) {
        setMessage(response.data.message);
        setName('');
        setEmail('');
        setPassword('');
        setConfirmPassword('');
        setStreet('');
        setCity('');
        setState('');
        setZipCode('');
        setRole('Customer');
      }
    } catch (error) {
      if (error.response && error.response.status === 409) {
        setMessage("An account with this email already exists. Please use another email.");
      } else {
        setMessage(`An error occurred: ${error.message}`);
      }
    }
  };

  return (
    <div className="unique-register-container">
      <div className="unique-register-box">
        <h2>Sign Up</h2>
        {message && <p className="unique-message">{message}</p>}
        <form onSubmit={handleSignup}>
          <div className="unique-form-group">
            <label htmlFor="name">Full Name</label>
            <input
              type="text"
              id="name"
              placeholder="Enter your full name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
          </div>

          <div className="unique-form-group">
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

          {/* Form row for address fields */}
          <div className="unique-form-row">
            <div className="unique-form-group">
              <label htmlFor="street">Street</label>
              <input
                type="text"
                id="street"
                placeholder="Enter your street"
                value={street}
                onChange={(e) => setStreet(e.target.value)}
                required
              />
            </div>

            <div className="unique-form-group">
              <label htmlFor="city">City</label>
              <input
                type="text"
                id="city"
                placeholder="Enter your city"
                value={city}
                onChange={(e) => setCity(e.target.value)}
                required
              />
            </div>
          </div>

          <div className="unique-form-row">
            <div className="unique-form-group">
              <label htmlFor="state">State</label>
              <input
                type="text"
                id="state"
                placeholder="Enter your state"
                value={state}
                onChange={(e) => setState(e.target.value)}
                required
              />
            </div>

            <div className="unique-form-group">
              <label htmlFor="zipCode">Zip Code</label>
              <input
                type="text"
                id="zipCode"
                placeholder="Enter your zip code"
                value={zipCode}
                onChange={(e) => setZipCode(e.target.value)}
                required
              />
            </div>
          </div>

          <div className="unique-form-group">
            <label htmlFor="password">Password</label>
            <input
              type={showPassword ? 'text' : 'password'}
              id="password"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <div className="unique-form-group">
            <label htmlFor="confirmPassword">Confirm Password</label>
            <input
              type={showPassword ? 'text' : 'password'}
              id="confirmPassword"
              placeholder="Confirm your password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              required
            />
          </div>

          <div className="unique-form-group">
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

          <div className="unique-password-options">
            <label>
              <input
                type="checkbox"
                onChange={togglePasswordVisibility}
              />
              Show Password
            </label>
          </div>

          <button type="submit" className="unique-register-button">Sign Up</button>
        </form>

        <p className="unique-login-text">
          Already have an account? <Link to="/login">Login here</Link>
        </p>
      </div>
    </div>
  );
};

export default Register;