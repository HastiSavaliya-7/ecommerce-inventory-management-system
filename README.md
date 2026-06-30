#  Ecommerce Inventory Management System

A Spring Boot based Ecommerce Inventory Management System that manages products, inventory, and customer orders with automated notifications and stock reports.

---

##  Features

- Add multiple products
- Restock products
- Place customer orders
- Automatic stock update
- Low stock alert
- Email notification
- WhatsApp notification using Twilio
- CSV stock report generation
- Scheduled daily stock report
- MySQL Database integration

---

##  Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- MySQL
- Maven
- Twilio API
- Java Mail Sender
- OpenCSV
- Git
- GitHub

---

##  Project Structure

```
src
 ├── controller
 ├── service
 ├── repository
 ├── model
 ├── DTO
 └── resources
```

---

##  REST APIs

### Product APIs

### Add Products

POST

```
/product/addProduct
```

### Restock Product

POST

```
/product/restock/{productId}/{newStock}
```

---

### Order API

POST

```
/order
```

---

##  Notifications

- Email Bill
- Email Stock Report
- Low Stock Alert
- WhatsApp Bill Notification

---

##  Report Generation

Current stock is exported as a CSV file automatically.

Example:

```
2026-03-17_stock.csv
```

---

##  Database

MySQL

Tables

- Product
- Orders

---

##  Configuration

Update your own credentials inside

```
application.properties
```

- MySQL
- Gmail App Password
- Twilio SID
- Twilio Token

---

##  Run Project

1. Clone Repository

```
git clone <repository-url>
```

2. Open in IntelliJ IDEA

3. Configure MySQL

4. Update application.properties

5. Run Application

---

##  Future Improvements

- JWT Authentication
- Role Based Access
- Payment Gateway Integration
- Swagger Documentation
- Docker Support

---

##  Developed By

**Hasti Savaliya**

Java Backend Developer
