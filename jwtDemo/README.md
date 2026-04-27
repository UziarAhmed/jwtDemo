# JWT Demo — Spring Boot

A Spring Boot REST API with JWT authentication, role-based access control, shopping cart, and Razorpay payment integration.

## Tech Stack
- Java 17
- Spring Boot 4.0.5
- Spring Security + JWT (jjwt 0.13.0)
- MySQL
- Razorpay

## Setup

### 1. Generate a JWT Secret
```bash
openssl rand -base64 32
```

### 2. Create your `.env` file
Copy `.env.example` and rename it to `.env`:
```bash
cp .env.example .env
```
Fill in your real values.

### 3. Run the project

**Windows CMD:**
```cmd
for /f "tokens=1,2 delims==" %i in (.env) do set %i=%j
mvnw spring-boot:run
```

**Windows PowerShell:**
```powershell
Get-Content .env | ForEach-Object { $k,$v = $_ -split '=',2; [System.Environment]::SetEnvironmentVariable($k,$v) }
.\mvnw spring-boot:run
```

**Linux / Mac:**
```bash
export $(cat .env | xargs)
./mvnw spring-boot:run
```

### 4. Test
```
POST http://localhost:8081/auth/register
POST http://localhost:8081/auth/login
```

## Security Notes
- Never commit `.env` to GitHub
- Regenerate JWT secret for production
- Use real Razorpay live keys for production  (test mode)
