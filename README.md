# Delivery Supervision System

A mobile and server-based delivery supervision solution designed to improve coordination between the control center and delivery agents.  
The project includes an Android application for **delivery drivers** and **controllers**, along with a backend API that manages deliveries, messages, authentication, and statistics [file:1].

## Project Overview

This system allows:
- Drivers to view their daily delivery route.
- Drivers to update delivery status and send urgent messages.
- Controllers to monitor all deliveries in real time.
- Controllers to view dashboards and communicate with drivers .

The solution follows a client-server architecture:
- The Android mobile application communicates with the backend REST API.
- The backend connects to the main database.
- A local SQLite database is used inside the Android app for offline or temporary storage of delivery data .

## Features

### Driver Features
- View today’s deliveries.
- See delivery details such as order number, client name, phone number, city, amount, and payment mode.
- Update delivery status.
- Add remarks when a delivery is not completed.
- Send urgent messages to the controller.
- Synchronize local data with the server .

### Controller Features
- View all deliveries globally.
- Search and filter deliveries.
- Monitor dashboards and statistics.
- Send information messages to drivers.
- View internal conversations with drivers .

## Technologies Used

### Frontend
- Android Studio
- Java
- RecyclerView
- **SQLite** for local mobile storage 

### Backend
- Spring Boot
- REST API
- JDBC / SQL access

### Other Tools
- Postman for API testing
- Oracle Database was also used in the academic project report and data model description 

## Database Architecture

### Local Database: SQLite
SQLite is used inside the Android application to store delivery data locally.  
It supports:
- Offline access to delivery information.
- Temporary caching of delivery records.
- Local updates before synchronization with the server .

### Server Database: Oracle
PostgreSQL is used as the central database on the backend side.  
It stores:
- Users and personnel data.
- Deliveries and orders.
- Messages.
- Statistics and tracking information .

## Main Modules

### Authentication
Users log in with their credentials.  
Depending on their role, they are redirected to:
- Driver interface.
- Controller interface .

### Delivery Management
Drivers can:
- View assigned deliveries.
- Open delivery details.
- Mark delivery status as delivered, cancelled, or in progress .

### Messaging
The system includes internal messaging between drivers and controllers, including urgent messages .

### Synchronization
The mobile app can synchronize local SQLite data with the server when the connection is available .

## Project Structure

A typical structure may look like this:

```bash
project-root/
├── backend/
│   ├── src/
│   ├── pom.xml
│   └── ...
├── frontend/
│   ├── app/
│   ├── build.gradle
│   └── ...
├── database/
│   ├── sqlite/
│   └── Oracle/
└── README.md
```

## Installation

### Prerequisites
- Android Studio
- Java 17 or compatible version
- Spring Boot environment
- Oracle server
- SQLite support in Android
- Postman (optional, for testing APIs)

### Backend Setup
1. Clone the repository.
2. Configure the Oracle connection in the backend application properties.
3. Import or create the required tables.
4. Run the Spring Boot application.

### Android App Setup
1. Open the Android project in Android Studio.
2. Update the backend API base URL if needed.
3. Build and run the application on an emulator or device.

## API Main Endpoints

The backend exposes endpoints for:
- Authentication
- Driver delivery list
- Delivery status updates
- Controller dashboards
- Messaging
- Personnel lookup 


## Offline Mode

The Android app uses SQLite to keep working even when the network is unstable or unavailable.  
When the connection is restored, the app can synchronize local data with the server .


## Future Improvements

- Add push notifications.
- Improve offline synchronization conflict handling.
- Add full search and advanced filtering.
- Add unit and integration tests.
- Improve UI/UX responsiveness.

## Authors

- MALEK AYED


This project was developed as part of a mobile development academic project and demonstrates delivery tracking, local storage, and backend integration [file:1].



[Final Project Report.pdf](https://github.com/user-attachments/files/28457988/Final.Project.Report.pdf)
