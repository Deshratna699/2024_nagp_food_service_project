# Food Delivery Application

## Operational Description
Users have the ability to locate a restaurant by its name, place orders by selecting menu items, specifying quantities, and providing dietary notes. Additionally, users can input delivery details. Upon order placement, the system generates an order summary containing the selected food items, quantities, prices, and the order timestamp. Users then proceed to complete the payment process by entering credit card details, including the card number, expiration date, and security code. Successful payment returns a payment ID and timestamp, marking the order as complete, allowing users to track the estimated delivery time.

## System Architecture
The application comprises three microservices: **food-order-service**, **order-processing-service**, and **order-payment-service**. Service registration and discovery utilize Netflix Eureka, and Hystrix serves as a circuit breaker.

**food-order-service** facilitates restaurant search and order placement. Without a front-end, orders are randomly generated when users send requests to http://localhost:9001/placeOrder. Restaurant and order data are stored in a MongoDB database.

**order-processing-service** takes orders and publishes them to the "orders" queue in RabbitMQ.

**order-payment-service** subscribes to the "orders" queue, validating payment details. Successful payments are confirmed and stored in a MySQL database.

These services run on ports 9001, 9002, and 9003 for **food-order-service**, **order-processing-service**, and **order-payment-service**, respectively. **Eureka** and **Hystrix-dashboard** are deployed on ports 8761 and 7979, while the RabbitMQ management interface is on port 15672.

## Running the Application
### Clone the Project
```bash
git clone https://github.com/zjuzhanxf/food-delivery-app.git
```

### Build
```bash
cd food-delivery-app
mvn clean install
```

### Start Containers
Launch MySQL, MongoDB, and RabbitMQ containers to store payment, restaurant, and order information, respectively.
```bash
docker-compose up
```

### Start Eureka
Navigate back to the food-delivery-app folder, then execute:
```bash
cd platform/eureka
java -jar target/eureka-0.0.1-SNAPSHOT.jar
```

### Start Hystrix Dashboard
Return to the food-delivery-app folder and run:
```bash
cd platform/hystrix-dashboard
java -jar target/hystrix-dashboard-1.4.5.RELEASE.jar
```

### Start Microservices
Start each microservice individually:
```bash
cd food-order-service/
java -jar target/food-order-service-1.0-SNAPSHOT.jar
```
```bash
cd order-processing-service/
java -jar target/order-processing-service-1.0-SNAPSHOT.jar
```
```bash
cd order-payment-service/
java -jar target/order-payment-service-1.0-SNAPSHOT.jar
```

### Upload Restaurant Information
Utilize Postman to post restaurant information to MongoDB. The data is stored in `food-delivery-app/food-order-service/src/main/resources/restaurants-init.json`. Visit localhost:9001 in the browser to view all restaurant information.

### Place an Order
Enter "localhost:9001/placeOrder" in the web browser's address field to place an order. Each refresh generates a new order. View order information at localhost:9001 and check service logs for details.

### RabbitMQ Management Interface
Access the RabbitMQ dashboard at http://localhost:15672/#/ using the credentials "guest" and "guest."

### Eureka and Hystrix-dashboard
Visit http://localhost:8761/ to see registered services. Monitor the status of **food-order-service** at http://localhost:7979/ by entering http://localhost:9001/hystrix.stream.

### PartB: Technical Documnet Deliverabies as per the requirement:
**1. Identified Microservices with Explanation:**

**Food Order Service:**
- This microservice handles the functionality of searching for restaurants and placing orders.
- It allows users to locate a restaurant by its name and place orders by selecting menu items, specifying quantities, and providing dietary notes.
- The service generates order summaries containing selected food items, quantities, prices, and timestamps upon order placement.
- Reasonable Explanation: This service encapsulates the core functionality related to the user interaction for placing orders, ensuring separation of concerns and modularity within the system.

**Order Processing Service:**
- Responsible for processing incoming orders from the food order service.
- It publishes received orders to a message queue (RabbitMQ) named "orders" for further processing.
- Ensures decoupling between order placement and payment processing.
- Reasonable Explanation: By offloading order processing to a separate service, the system gains scalability and flexibility, allowing for asynchronous processing of orders.

**Order Payment Service:**
- Subscribes to the "orders" queue in RabbitMQ to receive payment-related messages.
- Validates payment details and saves successful payments to a MySQL database.
- Manages the payment process securely and ensures the integrity of payment transactions.
- Reasonable Explanation: Separating payment processing into its own service enhances security and maintainability, allowing for independent scalability and updates.

**High Level Diagram:**
```
                                              +-------------------+
                            |                   |
               +---------->  food-order-service|
               |            |                   |
               |            +-------------------+
               |
               |
+--------------|------------+-------------------+
|              |            |                   |
|              |            |                   |
|              |            |                   |
|              |            |  order-processing-|
|    RabbitMQ  |            |     service       |
|              |            |                   |
|              |            |                   |
|              |            +-------------------+
|              |
|              |
+--------------|------------+-------------------+
               |            |                   |
               |            |                   |
               |            |                   |
               +----------> |order-payment-     |
                            |   service         |
                            |                   |
                            +-------------------+

```

**2. URL Definitions of the Scenarios (API Endpoints):**

**Food Order Service:**
- Endpoint: POST `/placeOrder`
- Request Payload: None (Orders are randomly generated)
- Response Payload: Order summary including selected food items, quantities, prices, and timestamps.

**Order Processing Service:**
- No direct API endpoint exposed. Listens to messages from the RabbitMQ queue "orders".

**Order Payment Service:**
- No direct API endpoint exposed. Listens to messages from the RabbitMQ queue "orders".

**3. API Gateway, Service Discovery Integration:**
- In this architecture, an API gateway (such as Netflix Zuul) can be integrated to provide a single entry point for clients to access the microservices.
- Service discovery is achieved using Netflix Eureka, which allows microservices to register themselves and discover other services dynamically.
- The API gateway would route requests to the appropriate microservice instances registered with Eureka, providing load balancing and fault tolerance.

**4. Inter-communications Approach & Assumptions:**

- **Message Queue (RabbitMQ)**: Assumption is made that RabbitMQ is used for asynchronous communication between microservices. For example, when an order is placed, the food order service sends the order details to the order processing service via RabbitMQ.
- **Service Registration & Discovery (Netflix Eureka)**: Microservices register themselves with Eureka upon startup, enabling dynamic service discovery. This allows microservices to locate and communicate with each other without hardcoded endpoints.
- **Circuit Breaker Pattern (Hystrix)**: Hystrix is employed to prevent cascading failures and provide fallback mechanisms in case of service disruptions, ensuring system reliability and fault tolerance.
- **Data Persistence Assumption**: MongoDB is used for storing restaurant and order data, while MySQL is used for storing payment-related information. Each microservice interacts with its designated database for data storage and retrieval, ensuring data integrity and isolation.