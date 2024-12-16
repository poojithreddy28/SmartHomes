import React from 'react';
import './trending.css'; // Make sure you create this CSS file

const Trending = () => {
  // Hardcoded data for trending products and sales
  const mostLikedProducts = [
    { name: "VisionPro5000", likes: 12, category: "Smart Doorbells", imagePath: "/images/all/VisionPro5000.jpeg" },
    { name: "CyberShield360", likes: 10, category: "Smart Doorlocks", imagePath: "/images/all/CyberShield360.jpeg" },
    { name: "EchoBlast360", likes: 7, category: "Smart Speakers", imagePath: "/images/all/EchoBlast360.jpeg" },
    { name: "AuraBrightRGB", likes: 3, category: "Smart Lightings", imagePath: "/images/all/AuraBrightRGB.jpeg" },
    { name: "ClimaGuardX1", likes: 3, category: "Smart Thermostats", imagePath: "/images/all/ClimaGuardX1.jpeg" }
  ];

  const mostSoldZipCodes = [
    { zipCode: "60601", sales: 9 },
    { zipCode: "60602", sales: 5},
    { zipCode: "60603", sales: 3},
    { zipCode: "60604", sales: 2},
    { zipCode: "60606", sales: 2 }
  ];

  const mostSoldProducts = [
    { name: "CyberShield360", sales: 16, category: "Smart Doorlocks", imagePath: "/images/all/CyberShield360.jpeg" },
    { name: "VisionPro5000", sales: 13, category: "Smart Doorbells", imagePath: "/images/all/VisionPro5000.jpeg" },
    { name: "LumiGlowPro", sales: 10, category: "Smart Lightings", imagePath: "/images/all/LumiGlowPro.jpeg" },
    { name: "PulseBeatVertical", sales: 6, category: "Smart Speakers", imagePath: "/images/all/PulseBeatVertical.jpeg" },
    { name: "ThermoCoreNeo", sales: 4, category: "Smart Thermostats", imagePath: "/images/all/ThermoCoreNeo.jpeg" }
];

  return (
    <div className="trending-container">
      <h2>Trending Products and Sales</h2>

      {/* Top five most liked products */}
      <div className="trending-section">
        <h3>Top 5 Most Liked Products</h3>
        <div className="trending-cards">
          {mostLikedProducts.map((product, index) => (
            <div key={index} className="trending-card">
              <img src={product.imagePath} alt={product.name} className="trending-product-image" />
              <div className="trending-product-info">
                <h4>{product.name}</h4>
                <p><strong>Category:</strong> {product.category}</p>
                <p><strong>Likes:</strong> {product.likes}</p>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Top five zip codes where the maximum number of products were sold */}
      <div className="trending-section">
        <h3>Top 5 Zip Codes with the Most Sales</h3>
        <div className="trending-cards">
          {mostSoldZipCodes.map((zip, index) => (
            <div key={index} className="trending-card">
              <p><strong>Zip Code:</strong> {zip.zipCode}</p>
              <p><strong>Total Sales:</strong> {zip.sales}</p>
            </div>
          ))}
        </div>
      </div>

      {/* Top five most sold products */}
      <div className="trending-section">
        <h3>Top 5 Most Sold Products</h3>
        <div className="trending-cards">
          {mostSoldProducts.map((product, index) => (
            <div key={index} className="trending-card">
              <img src={product.imagePath} alt={product.name} className="trending-product-image" />
              <div className="trending-product-info">
                <h4>{product.name}</h4>
                <p><strong>Category:</strong> {product.category}</p>
                <p><strong>Total Sold:</strong> {product.sales}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Trending;