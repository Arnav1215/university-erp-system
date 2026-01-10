
CREATE DATABASE IF NOT EXISTS auth_db;
USE auth_db;

CREATE TABLE IF NOT EXISTS users_auth (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    last_login DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username)
);




CREATE DATABASE IF NOT EXISTS university_erp;
USE university_erp;


CREATE TABLE IF NOT EXISTS students (
    user_id INT PRIMARY KEY,
    roll_no VARCHAR(20) UNIQUE NOT NULL,
    program VARCHAR(100),
    year INT,
    full_name VARCHAR(100),
    email VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES auth_db.users_auth(user_id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS instructors (
    user_id INT PRIMARY KEY,
    department VARCHAR(100),
    full_name VARCHAR(100),
    email VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES auth_db.users_auth(user_id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) UNIQUE NOT NULL,
    title VARCHAR(200) NOT NULL,
    credits INT NOT NULL,
    description TEXT
);


CREATE TABLE IF NOT EXISTS sections (
    section_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    instructor_id INT,
    day VARCHAR(20),
    time VARCHAR(50),
    room VARCHAR(50),
    capacity INT NOT NULL,
    semester VARCHAR(20) NOT NULL,
    year INT NOT NULL,
    enrolled_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (instructor_id) REFERENCES instructors(user_id) ON DELETE SET NULL,
    INDEX idx_semester_year (semester, year)
);

CREATE TABLE IF NOT EXISTS section_assessment_weights (
    section_id INT PRIMARY KEY,
    quiz_weight DECIMAL(5,2) DEFAULT 20,
    midterm_weight DECIMAL(5,2) DEFAULT 30,
    endsem_weight DECIMAL(5,2) DEFAULT 50,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (section_id) REFERENCES sections(section_id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    section_id INT NOT NULL,
    status VARCHAR(20) DEFAULT 'ENROLLED',
    enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    drop_deadline TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
    FOREIGN KEY (section_id) REFERENCES sections(section_id) ON DELETE CASCADE,
    UNIQUE KEY unique_enrollment (student_id, section_id),
    INDEX idx_student (student_id),
    INDEX idx_section (section_id)
);


CREATE TABLE IF NOT EXISTS grades (
    grade_id INT AUTO_INCREMENT PRIMARY KEY,
    enrollment_id INT NOT NULL,
    component VARCHAR(50) NOT NULL,
    score DECIMAL(5,2),
    max_score DECIMAL(5,2) DEFAULT 100,
    final_grade DECIMAL(5,2),
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id) ON DELETE CASCADE,
    INDEX idx_enrollment (enrollment_id)
);


CREATE TABLE IF NOT EXISTS settings (
    setting_key VARCHAR(50) PRIMARY KEY,
    setting_value VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);




INSERT INTO settings (setting_key, setting_value) VALUES ('maintenance_mode', 'false')
ON DUPLICATE KEY UPDATE setting_value = 'false';











USE auth_db;

INSERT INTO users_auth (username, role, password_hash, status) VALUES
('admin', 'ADMIN', '$2a$10$OJF5sgoeAy8YmqqI5bcc4.k0rTEwACLOfAcIYhSK84LPbv.YQiqVu', 'ACTIVE')
ON DUPLICATE KEY UPDATE username = VALUES(username);
