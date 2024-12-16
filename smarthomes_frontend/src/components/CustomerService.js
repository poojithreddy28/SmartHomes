import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import axios from 'axios';
import Navbar from './navbar';
import CircularProgress from '@mui/material/CircularProgress';
import './customerService.css';

const CustomerService = () => {
  const location = useLocation();
  const username = location.state?.username || '';

  const [activeTab, setActiveTab] = useState('openTicket');
  const [orderId, setOrderId] = useState('');
  const [orders, setOrders] = useState([]);
  const [query, setQuery] = useState('');
  const [file, setFile] = useState(null);
  const [filePreview, setFilePreview] = useState(null);
  const [ticketStatus, setTicketStatus] = useState('');
  const [ticketId, setTicketId] = useState('');
  const [ticketDetails, setTicketDetails] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isLoadingStatus, setIsLoadingStatus] = useState(false);

  useEffect(() => {
    const fetchOrders = async () => {
      if (username) {
        try {
          const response = await axios.get(`http://localhost:8000/smarthomes_backend/orders?username=${username}`);
          setOrders(response.data);
        } catch (error) {
          console.error('Error fetching orders:', error);
        }
      }
    };
    fetchOrders();
  }, [username]);

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    setFile(selectedFile);
    setFilePreview(URL.createObjectURL(selectedFile));
  };

  const handleSubmitTicket = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);

    const formData = new FormData();
    formData.append('username', username);
    formData.append('orderId', orderId);
    formData.append('query', query);
    formData.append('file', file);

    try {
      const response = await axios.post('http://localhost:8000/smarthomes_backend/CustomerServiceServlet', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });

      if (response.status === 200) {
        const { ticketId } = response.data;
        setTicketId(ticketId);
        setTicketStatus('Submitted successfully');
      } else {
        setTicketStatus('Submission failed');
      }
    } catch (error) {
      console.error('Error submitting ticket:', error);
      setTicketStatus('Submission failed');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCheckStatus = async () => {
    setIsLoadingStatus(true);
    try {
      const response = await axios.get(`http://localhost:8000/smarthomes_backend/CustomerServiceServlet?ticketId=${ticketId}`);
      
      if (response.status === 200 && response.data) {
        setTicketDetails(response.data);
        setTicketStatus('Fetched');
      } else {
        setTicketStatus('Ticket not found');
      }
    } catch (error) {
      console.error('Error fetching ticket status:', error);
      setTicketStatus('Ticket not found');
    } finally {
      setIsLoadingStatus(false);
    }
  };

  return (
    <div className="customer-service-container">
      <Navbar username={username} className="fixed-navbar"/>
      <div className="customer-service-content">
        <div className="button-container">
          <button
            className={`tab-button ${activeTab === 'openTicket' ? 'active' : ''}`}
            onClick={() => setActiveTab('openTicket')}
          >
            Open a Ticket
          </button>
          <button
            className={`tab-button ${activeTab === 'statusTicket' ? 'active' : ''}`}
            onClick={() => setActiveTab('statusTicket')}
          >
            Status of a Ticket
          </button>
        </div>

        {activeTab === 'openTicket' ? (
          <form onSubmit={handleSubmitTicket} className="ticket-form">
            <h2>Open a Ticket</h2>
            <div className="form-group">
              <label>Order ID</label>
              <select 
                value={orderId} 
                onChange={(e) => setOrderId(e.target.value)} 
                required
                className="styled-input"
              >
                <option value="" disabled>Select an Order ID</option>
                {orders.map((order) => (
                  <option key={order.orderId} value={order.orderId}>
                    Order ID: {order.orderId}
                  </option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Upload a picture of the item/package</label>
              <input
                type="file"
                onChange={handleFileChange}
                required
                className="styled-input"
              />
              {filePreview && (
                <img
                  src={filePreview}
                  alt="Selected file preview"
                  className="file-preview"
                />
              )}
            </div>
            <div className="form-group">
              <label>Describe your issue</label>
              <textarea
                placeholder="Enter additional details here..."
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                required
                className="styled-input"
              ></textarea>
            </div>
            <button type="submit" className="submit-button" disabled={isSubmitting}>
              {isSubmitting ? <CircularProgress size={24} color="inherit" /> : 'Submit'}
            </button>
            {ticketId && !isSubmitting && (
              <p className="ticket-number">Your Ticket Number: {ticketId}</p>
            )}
            {ticketStatus && (
              <p className="ticket-status-message">{ticketStatus}</p>
            )}
          </form>
        ) : (
          <div className="status-check-form">
            <h2>Status of a Ticket</h2>
            <div className="form-group">
              <label>Enter Ticket Number</label>
              <input
                type="text"
                placeholder="Enter your Ticket Number"
                value={ticketId}
                onChange={(e) => setTicketId(e.target.value)}
                className="styled-input"
              />
            </div>
            <button onClick={handleCheckStatus} className="check-status-button" disabled={isLoadingStatus}>
              {isLoadingStatus ? <CircularProgress size={24} color="inherit" /> : 'Check Status'}
            </button>
            {ticketStatus && (
              <div className="ticket-status-container">
                {ticketDetails ? (
                  <div className="ticket-details">
                    <p><strong>Order Number:</strong> {ticketDetails.order_id}</p>
                    <p><strong>Ticket Description:</strong> {ticketDetails.query}</p>
                    <p className='decision'><strong>Decision:</strong> {ticketDetails.response}</p>
                  </div>
                ) : (
                  <p>{ticketStatus}</p>
                )}
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default CustomerService;