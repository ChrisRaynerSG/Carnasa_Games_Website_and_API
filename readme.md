<p align = "center">
  <img src="https://c10.patreonusercontent.com/4/patreon-media/p/reward/6427525/ec27b79864154c408c96a3da0060597c/eyJ3Ijo0MDB9/1.png?token-time=2145916800&token-hash=n97ZZzLYnoGyf-NSqMVqa4OgIejf7EKiybVuk9xyPYA%3D" alt="logo" width = 250px>
</p>
<p align = "center">
  <h1 align = "center">Carnasa Games Front End and API</h1>
</p>

---

# Carnasa Game API Documentation

## Overview

The **Carnasa Game API** is a RESTful API developed using Spring to provide backend functionality for the Carnasa Games Web App. This API facilitates interaction with a MySQL database, managing data related to users, games, comments, and followers. It is designed to securely connect the front end with the database, offering various endpoints for managing and accessing the stored information.

## Setup

To set up the Carnasa Game API, follow these steps:

1. **Create an `application.properties` File**:
   
   In the `src/main/resources` directory of your project, create a file named `application.properties` and add the following properties:

   ```properties
   spring.application.name=CarnasaGamesWebsiteAndApi
   spring.datasource.username={YOUR_MY_SQL_USERNAME}
   spring.datasource.password={YOUR_MY_SQL_PASSWORD}
   spring.jpa.hibernate.ddl-auto=update
   spring.datasource.url=jdbc:mysql://{YOUR_MY_SQL_DB_LOCATION}/games_website?createDatabaseIfNotExist=true
   server.port={PORT_TO_USE}
   ```
   Replace the placeholders with your actual configuration:

    - **{YOUR_MY_SQL_USERNAME}**: Your MySQL username.
    - **{YOUR_MY_SQL_PASSWORD}**: Your MySQL password.
    - **{YOUR_MY_SQL_DB_LOCATION}**: The location of your MySQL database (e.g., `localhost:3306`).
    - **{PORT_TO_USE}**: The port number on which you want the application to run (e.g., `8080`).

2. **Run the Application:**

    After setting up the `application.properties` file, you can run the Spring Boot application. The API will connect to your specified MySQL database and start the server on the port you defined.

3. **Access the API:**

    Once the application is running, you can start interacting with the Carnasa Game API using the configured endpoints. The database will initially be empty but can be populated using the API `POST` methods , or manually inserting data into the database using an IDE or MySQL Workbench or similar.


## Features

- **Database Management**: The API connects to a MySQL database, handling CRUD (Create, Read, Update, Delete) operations for:
  - Users
  - Games
  - Comments
  - High scores

- **Security**: 
  - Utilizes **JSON Web Tokens (JWT)** for authentication and authorization.
  - JWTs can be generated via the login endpoint.
  - Secure access control ensuring that:
    - Only Admins and the specific user can view private accounts.
    - Admins have full authority over the database, including updating and deleting any record.
    - Regular users can only modify or delete their own content, including games, comments, and follower relationships.

- **Anonymous Access**:
  - Allows anonymous users to browse games, comments, and public user profiles.
  - New users can create an account through an unsecured `POST /users` endpoint.

## API Documentation

The Carnasa Game API is fully documented using **OAPI 3.0**. To access the complete documentation of all endpoints, follow these steps:

1. **Visit Swagger**:
   - Open your web browser and navigate to the Swagger UI at `http://{YOUR_SERVER_ADDRESS}/swagger-ui.html`.
   - Replace `{YOUR_SERVER_ADDRESS}` with the address where your API is hosted (e.g., `localhost:8080`).

2. **Explore the Endpoints**:
   - The Swagger interface provides a detailed overview of all available endpoints, including their request and response formats.

3. **Authorize and Test Secure Endpoints**:
   - Swagger UI allows you to authorize using your JWT token.
   - Once authorized, you can directly test secure endpoints within the Swagger interface.

Swagger offers a user-friendly interface to interact with the API, making it easy to explore and test the functionality of the Carnasa Game API.

## Security

- **JSON Web Tokens (JWT)** are used to secure the API. Tokens are generated upon successful login and must be included in the `Authorization` header of requests to protected endpoints.

- **Unsecured Endpoints**:
  - `POST /users`: Allows new users to register without requiring a JWT.

- **Access Control**:
  - Private user accounts are restricted to the account owner and Admins.
  - Admins can perform any action on the API, while regular users are limited to managing their own content.


---

# Carnasa Games Front End 

To be implemented

---

For further assistance, please contact the development team or refer to the project repository.
