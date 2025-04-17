# PriceTrackNotif
### Overview
The PriceTrackNotif is a Spring Boot application designed to monitor product prices and notify users via email when their desired price conditions are met. This system leverages RESTful APIs, a scheduler for periodic checks, and email notifications.

### Features
- Add and configure price alerts for products.
- Periodic price checking using a scheduler.
- Email notifications for price drops.
- Persist alerts using a relational database.
- Flexible notification frequency (e.g., ALWAYS-Send alert throughout the day, MORNING-Send alert only morning between 6 am and 9 am, MIDNIGHT-Send alert only midnight between 1 am and 3 am).

### Technologies Used
- **Java** (Spring Boot Framework)
- **Spring Scheduler** for periodic tasks
- **Spring Data JPA** for database persistence
- **Spring Mail** for email notifications
- **H2 Database** for testing (default)

 ### Additional details
API documentation, sample requests, and responses are located in the PriceTrackNotif/sample folder.
For security, spring.mail.password is not included in application.properties. You must add your email and app-specific password to enable email notifications.
