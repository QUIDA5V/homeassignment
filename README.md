# 📦 Home Assignment

> This is a Spring Boot application developed as part of a backend engineering home assignment. 

## 🚀 Setup & Run Instructions

### 📋 Prerequisites
- Java 17+
- Maven 3.8+
- Git (optional)

### 🛠️ Clone the repository:
- git clone https://github.com/QUIDA5V/homeassignment.git
- cd homeassignment

### 🛠️ Build & Run
- mvn clean package

### 🛠️ Run test
- mvn test
- 
### 🛠️ Run the application
- mvn spring-boot:run

The application will be available at http://localhost:8080

## 💡 Usage

- Set an Event with status LIVE :
curl -X POST http://localhost:8080/event/status \
-H "Content-Type: application/json" \
-d '{"eventId": "9eb0999d-9282-4ae5-95b2-324a866e3e55", "status": "LIVE"}'

- Set an Event with status NOT_LIVE :
  curl -X POST http://localhost:8080/event/status \
  -H "Content-Type: application/json" \
  -d '{"eventId": "9eb0999d-9282-4ae5-95b2-324a866e3e55", "status": "NOT_LIVE"}'

- Get the status from an Event :
  curl http://localhost:8080/event/status/9eb0999d-9282-4ae5-95b2-324a866e3e55

## 🚀 Summary of technical decisions.

- Given that many Events might be at LIVE status at the same time and that the external api call should be stopped when the status is updated to NOT_LIVE I decided 
  to use TaskScheduler and ThreadPoolTaskScheduler to handle the scheduled external API call as a separate task for each event
  , due to the fact that is not possible to stop a task using @Scheduled.
- Having in mind the previous point , when the status is updated to NOT_LIVE , the scheduled external API call is stopped.
- Manual retry process is being used instead of using properties on Kafka Producer to send message , in order to handle unit test appropriately.
- @ControllerAdvice is used to error handling
- TODO -> Implement Protobuf serialization to compact the message and improve the performance.

## 📷 Screenshots
<img width="592" alt="Screenshot 2025-06-05 at 12 40 51 PM" src="https://github.com/user-attachments/assets/94242835-a366-496b-9477-89d60257c5fe" />

<img width="604" alt="Screenshot 2025-06-05 at 12 41 35 PM" src="https://github.com/user-attachments/assets/84614716-322d-41bd-8def-b324af0a2143" />

