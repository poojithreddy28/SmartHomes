
# SmartHomes E-Commerce Application

## Project Description

SmartHomes is a full-stack e-commerce web application developed using **Java Servlets** (backend), **React.js** (frontend), and **Python** services for integrating Gen AI tools and Elasticsearch. The platform supports various functionalities for customers, salesmen, and store managers. Features include product management, order placement, reviews, analytics, ticket handling, and advanced search and recommendations using **OpenAI embeddings**.

The application also integrates **Docker** for containerization and **JavaMail** for order confirmation and cancellation emails.

---

## Features

### Core Features
- **User Roles**: StoreManager, Customers, Salesmen.
- **Product Management**: Add/Delete/Update products.
- **Customer Account Management**: Create/Delete/Update accounts.
- **Order Management**: Place, cancel, and check the status of orders.
- **Product Reviews**: Customers can submit and view reviews.
- **Warranty**: Purchase additional warranties for products.
- **Cart Management**: Add or remove items in the cart within the session.

### Advanced Features
1. **Customer Service**:
   - Open tickets with images and text.
   - Status check of tickets (Refund, Replace, or Escalate).
   - Decision-making using **OpenAI GPT-4o-mini**.
2. **Search and Recommendations**:
   - Semantic search for product reviews and recommendations.
   - Powered by **Elasticsearch** and **OpenAI embeddings**.
3. **Reports**:
   - Inventory and Sales Reports with **Google Charts**.
4. **Email Notifications**:
   - JavaMail used for sending email notifications for order placement and cancellations.

---

## Prerequisites

Ensure you have the following installed:
- **Java** (OpenJDK 17.0.12 or above)
- **Apache Tomcat 9.x**
- **Node.js** (v18 or higher)
- **Python** (3.8 or higher)
- **MySQL** (8.x)
- **MongoDB** (Compass preferred for GUI)
- **Docker**
- **Elasticsearch**
- **npm** (for React dependencies)

---

## Installation and Setup

### Clone the Repository

```bash
git clone https://github.com/YourUsername/SmartHomes.git
cd SmartHomes
```

### Backend (Java Servlets)
1. Compile the Java Servlets:
   ```bash
   javac -d WEB-INF/classes src/com/smarthomes/*.java
   ```
2. Deploy the WAR file to Tomcat:
   - Place the WAR file in `webapps/` of your Tomcat directory.
   - Start Tomcat server:
     ```bash
     catalina.sh start
     ```

### Frontend (React.js)
1. Navigate to the frontend folder:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the React development server:
   ```bash
   npm start
   ```

### Python Services
1. Navigate to the Python services directory:
   ```bash
   cd python_services
   ```
2. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```
3. Start the Python Flask API:
   ```bash
   python app.py
   ```

### Databases
1. **MySQL**:
   - Import the provided `schema.sql` file to set up tables.
   ```bash
   mysql -u root -p < schema.sql
   ```
2. **MongoDB**:
   - Run the Python script `mongo_setup.py` to populate the MongoDB database.
3. **Elasticsearch**:
   - Start Elasticsearch via Docker:
     ```bash
     docker run -d -p 9200:9200 -e "discovery.type=single-node" elasticsearch:7.17.0
     ```
   - Use `elasticsearch_loader.py` to load product embeddings.

---

## Configuration

### Environment Variables

Set up the following environment variables in your system:
- **MySQL**:
  - `DB_HOST`, `DB_USER`, `DB_PASSWORD`, `DB_NAME`
- **MongoDB**:
  - `MONGO_URI`
- **OpenAI**:
  - `OPENAI_API_KEY`
- **JavaMail**:
  - `MAIL_USERNAME`, `MAIL_PASSWORD`

### Docker Setup

To containerize the application:
1. Build Docker images:
   ```bash
   docker-compose build
   ```
2. Start containers:
   ```bash
   docker-compose up
   ```

---

## Usage

### Access the Application
1. **Frontend**:
   - Open [http://localhost:3000](http://localhost:3000) in your browser.
2. **Backend**:
   - Ensure the Tomcat server is running.
3. **Python API**:
   - Ensure Flask API is running on port `5000`.

### Key Functionalities
1. **Customer Service**:
   - Navigate to the "Customer Service" tab.
   - Submit a ticket with text and an image.
   - View ticket status and decisions (Refund/Replace/Escalate).
2. **Search and Recommendations**:
   - Use "Search Reviews" or "Recommend Product" buttons.
   - Enter keywords for semantic searches.
3. **Order Management**:
   - Add items to the cart and proceed to checkout.
   - Receive an email confirmation for placed orders.
4. **Inventory and Sales Reports**:
   - Accessible via the Store Manager dashboard.

---

## Key Classes and Scripts

1. **Java**:
   - `MySQLDataStoreUtilities.java`: MySQL operations.
   - `MongoDBDataStoreUtilities.java`: MongoDB operations.
   - `CustomerServiceServlet.java`: Handles ticket operations.
2. **Python**:
   - `app.py`: Flask API for LLM and Elasticsearch interactions.
   - `elasticsearch_loader.py`: Embedding upload to Elasticsearch.
3. **Frontend**:
   - `CustomerService.js`: Handles customer service UI and backend calls.
   - `SearchAndRecommend.js`: Search and recommendation UI.

---

