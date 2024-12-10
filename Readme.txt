
Smart Homes Application - React Frontend & Java Servlet Backend

This is a full-stack web application that allows users to manage and purchase smart home devices. The application consists of a React frontend and a Java Servlet backend, with data stored in a MySQL database and Mongo DB.

---------------------------------Assignment 5 Functionalities---------------------------------

Recommend Product:
- Users can input preferences or keywords to receive product recommendations tailored to their needs.
- Recommendations are powered by OpenAI embeddings and Elasticsearch for semantic relevance.

Search Reviews:
- Users can search for product reviews using semantic queries.
- reviews are indexed in Elasticsearch for efficient and accurate search reviews.

Technologies Used:
- Frontend: React
- Backend: Java Servlets, MySQL
Database:
- MySQL: Stores structured application data (users, orders, products, tickets etc).
- MongoDB: Manages unstructured review data.

AI and Search:
- Elasticsearch: Powers semantic search and recommendations.
- gpt-4o-mini: Provides AI-driven responses for support tickets.

Tools and Infrastructure:
- Docker: Runs Elasticsearch container for search functionality.
- Tomcat Server: Hosts the Java backend.
- npm: Manages React frontend dependencies.

Prerequisites:
Ensure the following tools are installed on your machine:
- Java 8 or higher
- Node.js and npm
- MySQL
- Tomcat Server (version 9)

---

Installation & Setup:

1. Database Setup (MySQL):

- Open MySQL Workbench or another MySQL client.
- Create a new database, e.g., SmartHomes.
- Import the SQL schema file SmartHomes.sql to create the necessary tables (e.g., customers, orders, order_items, products, stores, tickets).

  SOURCE /path/to/SmartHomes.sql;

---

2. Backend (Java Servlet):

- Download and unzip the SmartHomesBackend folder.
- Open a terminal/command prompt and navigate to the backend project directory.

Build the Project:
Compile the Java servlet files:

  javac -d WEB-INF/classes src/com/smarthomes/*.java

Deploy the Servlet:

- Copy the SmartHomesBackend folder into the webapps directory of your Tomcat installation.

Run the Tomcat Server:

- Start the Tomcat server by navigating to the Tomcat bin folder and executing:

  - For Windows:
      ./startup.bat
  - For Linux/Mac:
      ./startup.sh

- The backend runs on port 8000. Ensure this port is open.

---

3. Frontend (React):

- Download and unzip the SmartHomes frontend folder.
- Open a terminal and navigate to the frontend project directory.

Install Node Modules:

- Run the following command to install the necessary dependencies:

  npm install

Start the Application:

- Run the development server with:

  npm start

- This will start the React application on port 3000.

---

Accessing the Application:

- Open your browser and navigate to the React frontend:

  http://localhost:3000/

- The backend API is accessible at:

  http://localhost:8000/smarthomes_backend/

---

Setting Up Elasticsearch and Semantic Search

Run docker-compose.yml using the below command

docker-compose up -d

- It creates elastic search container and runs on port 9200

Verify that Elasticsearch is running by visiting the following URL in your browser:

http://localhost:9200/

Running DataGenerator.py:

This script generates sample data using chatgpt LLM  4omini15 products and 5 reviews for each generated product.

Command  :  python DataGenerator.py

Running ElasticSemanticSearch.py:

This script creates embeddings for the data and indexes them into Elasticsearch.

Command : python ElasticSemanticSearch.py

Additional Notes:
- Elasticsearch must be running on port 9200 before executing the data generation and indexing scripts.
- Ensure that both the frontend (port 3000) and backend (port 8000) are running simultaneously.
- Database: Ensure the MySQL database is set up with the correct schema and data to support product, order, user, and ticket management.


