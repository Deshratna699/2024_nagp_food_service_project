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