# AuthPOC

A full-stack authentication proof-of-concept using Spring Boot (Java) for the backend and React for the frontend, featuring face recognition login.

## Features
- Username/password authentication
- Face recognition verification using webcam
- Role-based routing (ADMIN/user)
- Secure backend with HTTPS
- Docker support for backend

## Technologies
- Backend: Spring Boot, Java, PostgreSQL
- Frontend: React, face-api.js, Tailwind CSS
- Docker for backend deployment

## Getting Started

### Prerequisites
- Java 17+
- Node.js & npm
- PostgreSQL
- Docker (optional)

### Backend Setup
1. Install dependencies:
   ```sh
   cd backend
   mvn clean package
   ```
2. Configure database in `src/main/resources/application.yml`.
3. Generate SSL certificate for HTTPS:
   ```sh
   keytool -genkeypair -alias springboot -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore src/main/resources/keystore.p12 -validity 3650
   ```
4. Run backend:
   ```sh
   java -jar target/backend.jar
   ```
5. Or build and run with Docker:
   ```sh
   docker build -t authpoc-backend .
   docker run -p 8443:8443 authpoc-backend
   ```

### Frontend Setup
1. Install dependencies:
   ```sh
   cd frontend
   npm install
   ```
2. Start frontend:
   ```sh
   npm start
   ```
3. Access the app at `http://localhost:3000` (frontend) and `https://localhost:8443` (backend).

## Usage
- Register a new user.
- Login with username/password and face verification.
- ADMIN users are routed to `/users`, regular users to `/protected`.

## Project Structure
```
backend/
  src/main/java/...         # Spring Boot source
  src/main/resources/      # Configs, keystore
  target/backend.jar       # Compiled JAR
frontend/
  src/Components/          # React components
  src/services/api.js      # API calls
  public/models/           # face-api.js models
```

## Face Recognition
- Uses `face-api.js` in the frontend for face detection and descriptor extraction.
- Models are loaded from `/models` directory in `public`.

## HTTPS
- Backend runs on HTTPS (port 8443).
- Frontend API calls use `https://localhost:8443`.

## License
MIT
