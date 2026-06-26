# University ERP System

A comprehensive Enterprise Resource Planning system for university management built with Java Swing and MySQL.

## 🎓 Overview

The University ERP System is a desktop application designed to manage students, instructors, courses, and grades in an educational institution. It features role-based access control, real-time enrollment management, and comprehensive grade tracking.

## ✨ Features

### 🔐 Authentication & Security
- BCrypt password hashing with salt
- Role-based access control (Admin, Instructor, Student)
- Account lockout protection (5 attempts, 5-minute timeout)
- Secure session management

### 👨‍🎓 Student Management
- Course registration with real-time capacity checking
- Grade tracking with multi-component assessments
- Transcript generation (PDF/CSV)
- Academic progress monitoring

### 👨‍🏫 Instructor Management
- Section assignment and management
- Grade entry for multiple assessment components
- Weighted grade calculation
- Class performance analytics

### 🛠️ Administrative Features
- User management (Students, Instructors, Admins)
- Course catalog management
- Section scheduling and capacity control
- System maintenance mode

## 🏗️ Architecture

- **Frontend**: Java Swing with FlatLaf modern UI theme
- **Backend**: Java 17 with layered architecture (UI → API → Service → DAO)
- **Database**: MySQL with dual-database design (auth_db + university_erp)
- **Build Tool**: Maven
- **Connection Pooling**: HikariCP

## 📋 Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## 🚀 Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/Arnav1215/university-erp-system.git
cd university-erp
```

### 2. Database Setup
```sql
-- Create databases
CREATE DATABASE auth_db;
CREATE DATABASE university_erp;

-- Run the schema file
mysql -u root -p < db/schema.sql

-- Optional: Add sample data
mysql -u root -p < add_iiitd_courses.sql
mysql -u root -p < add_500_students.sql
mysql -u root -p < register_students_courses.sql
```

### 3. Configure Database Connection
Edit `src/main/resources/config.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/university_erp?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true
db.user=your_username
db.password=your_password
db.maximumPoolSize=6
db.minimumIdle=2
```

### 4. Build and Run
```bash
# Compile the project
mvn clean compile

# Run the application
mvn exec:java -Dexec.mainClass="edu.univ.erp.ui.MainApp"

# Or build JAR and run
mvn clean package
java -jar target/university-erp-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## 📊 System Statistics

- **500+** Students across 4 academic years
- **100+** Courses across 15+ departments
- **200+** Sections with capacity management
- **1,400+** Course enrollments
- **Multi-component** grade tracking system

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| Language | Java | 17 |
| UI Framework | Swing + FlatLaf | 3.4.1 |
| Database | MySQL | 8.0+ |
| Connection Pool | HikariCP | 5.0.1 |
| Build Tool | Maven | 3.6+ |
| Password Hashing | JBCrypt | 0.4 |
| CSV Export | OpenCSV | 5.7.1 |
| PDF Export | OpenPDF | 1.3.30 |
| Logging | SLF4J | 1.7.36 |

## 📁 Project Structure

```
src/main/java/edu/univ/erp/
├── ui/           # User Interface Layer
├── api/          # API Layer (6 modules)
├── service/      # Business Logic Layer
├── dao/          # Data Access Layer
├── domain/       # Entity Models
├── util/         # Utility Classes
├── auth/         # Authentication System
├── access/       # Access Control
└── exception/    # Custom Exceptions
```

## 🎯 Key Features

### Grade Management
- Configurable assessment weights (Quiz 20%, Midterm 30%, Final 50%)
- Automatic GPA/SGPA calculation
- Letter grade conversion (A+ to F scale)
- Semester-wise transcript generation

### Enrollment System
- 7-day add/drop windows
- Real-time capacity management
- Conflict resolution
- Deadline enforcement

### Security Features
- SQL injection prevention
- Input validation framework
- Transaction management
- Comprehensive error handling


## 🙏 Acknowledgments

- FlatLaf for the modern UI theme
- HikariCP for efficient connection pooling
- OpenCSV and OpenPDF for export functionality
