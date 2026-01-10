
USE auth_db;

INSERT IGNORE INTO users_auth (username, role, password_hash, status) VALUES

('2021001', 'STUDENT', '$2a$10$N9qo8uLOickgx2ZMRZoMye/Zo1jvK/YpJGSD4d0/.OG3rEBTHyQ2a', 'ACTIVE'),
('2021002', 'STUDENT', '$2a$10$N9qo8uLOickgx2ZMRZoMye/Zo1jvK/YpJGSD4d0/.OG3rEBTHyQ2a', 'ACTIVE'),
('2021003', 'STUDENT', '$2a$10$N9qo8uLOickgx2ZMRZoMye/Zo1jvK/YpJGSD4d0/.OG3rEBTHyQ2a', 'ACTIVE'),
('2021004', 'STUDENT', '$2a$10$N9qo8uLOickgx2ZMRZoMye/Zo1jvK/YpJGSD4d0/.OG3rEBTHyQ2a', 'ACTIVE'),
('2021005', 'STUDENT', '$2a$10$N9qo8uLOickgx2ZMRZoMye/Zo1jvK/YpJGSD4d0/.OG3rEBTHyQ2a', 'ACTIVE'),
('2021006', 'STUDENT', '$2a$10$N9qo8uLOickgx2ZMRZoMye/Zo1jvK/YpJGSD4d0/.OG3rEBTHyQ2a', 'ACTIVE'),
('2021007', 'STUDENT', '$2a$10$N9qo8uLOickgx2ZMRZoMye/Zo1jvK/YpJGSD4d0/.OG3rEBTHyQ2a', 'ACTIVE'),
('2021008', 'STUDENT', '$2a$10$N9qo8uLOickgx2ZMRZoMye/Zo1jvK/YpJGSD4d0/.OG3rEBTHyQ2a', 'ACTIVE'),
('2021009', 'STUDENT', '$2a$10$N9qo8uLOickgx2ZMRZoMye/Zo1jvK/YpJGSD4d0/.OG3rEBTHyQ2a', 'ACTIVE'),
('2021010', 'STUDENT', '$2a$10$N9qo8uLOickgx2ZMRZoMye/Zo1jvK/YpJGSD4d0/.OG3rEBTHyQ2a', 'ACTIVE');





CREATE TEMPORARY TABLE numbers AS
SELECT 
    @row_number := @row_number + 1 AS n
FROM 
    (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t1,
    (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t2,
    (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t3,
    (SELECT @row_number := 10) r
WHERE @row_number < 500;


INSERT IGNORE INTO users_auth (username, role, password_hash, status)
SELECT 
    CONCAT('2021', LPAD(n, 3, '0')) as username,
    'STUDENT' as role,
    '$2a$10$N9qo8uLOickgx2ZMRZoMye/Zo1jvK/YpJGSD4d0/.OG3rEBTHyQ2a' as password_hash,
    'ACTIVE' as status
FROM numbers 
WHERE n BETWEEN 11 AND 100;


INSERT IGNORE INTO users_auth (username, role, password_hash, status)
SELECT 
    CONCAT('2022', LPAD(n, 3, '0')) as username,
    'STUDENT' as role,
    '$2a$10$N9qo8uLOickgx2ZMRZoMye/Zo1jvK/YpJGSD4d0/.OG3rEBTHyQ2a' as password_hash,
    'ACTIVE' as status
FROM numbers 
WHERE n BETWEEN 1 AND 125;


INSERT IGNORE INTO users_auth (username, role, password_hash, status)
SELECT 
    CONCAT('2023', LPAD(n, 3, '0')) as username,
    'STUDENT' as role,
    '$2a$10$N9qo8uLOickgx2ZMRZoMye/Zo1jvK/YpJGSD4d0/.OG3rEBTHyQ2a' as password_hash,
    'ACTIVE' as status
FROM numbers 
WHERE n BETWEEN 1 AND 125;


INSERT IGNORE INTO users_auth (username, role, password_hash, status)
SELECT 
    CONCAT('2024', LPAD(n, 3, '0')) as username,
    'STUDENT' as role,
    '$2a$10$N9qo8uLOickgx2ZMRZoMye/Zo1jvK/YpJGSD4d0/.OG3rEBTHyQ2a' as password_hash,
    'ACTIVE' as status
FROM numbers 
WHERE n BETWEEN 1 AND 150;


USE university_erp;


INSERT IGNORE INTO students (user_id, roll_no, program, year, full_name, email)
SELECT 
    u.user_id,
    u.username as roll_no,
    CASE 
        WHEN (u.user_id % 3) = 0 THEN 'B.Tech Computer Science & Engineering'
        WHEN (u.user_id % 3) = 1 THEN 'B.Tech Electronics & Communication Engineering'
        ELSE 'B.Tech Computer Science & Design'
    END as program,
    4 as year,
    CONCAT('Student ', u.username) as full_name,
    CONCAT(u.username, '@iiitd.ac.in') as email
FROM auth_db.users_auth u
WHERE u.role = 'STUDENT' AND u.username LIKE '2021%';


INSERT IGNORE INTO students (user_id, roll_no, program, year, full_name, email)
SELECT 
    u.user_id,
    u.username as roll_no,
    CASE 
        WHEN (u.user_id % 4) = 0 THEN 'B.Tech Computer Science & Engineering'
        WHEN (u.user_id % 4) = 1 THEN 'B.Tech Electronics & Communication Engineering'
        WHEN (u.user_id % 4) = 2 THEN 'B.Tech Computer Science & Design'
        ELSE 'B.Tech Computer Science & Applied Mathematics'
    END as program,
    3 as year,
    CONCAT('Student ', u.username) as full_name,
    CONCAT(u.username, '@iiitd.ac.in') as email
FROM auth_db.users_auth u
WHERE u.role = 'STUDENT' AND u.username LIKE '2022%';


INSERT IGNORE INTO students (user_id, roll_no, program, year, full_name, email)
SELECT 
    u.user_id,
    u.username as roll_no,
    CASE 
        WHEN (u.user_id % 4) = 0 THEN 'B.Tech Computer Science & Engineering'
        WHEN (u.user_id % 4) = 1 THEN 'B.Tech Electronics & Communication Engineering'
        WHEN (u.user_id % 4) = 2 THEN 'B.Tech Computer Science & Design'
        ELSE 'B.Tech Computer Science & Biosciences'
    END as program,
    2 as year,
    CONCAT('Student ', u.username) as full_name,
    CONCAT(u.username, '@iiitd.ac.in') as email
FROM auth_db.users_auth u
WHERE u.role = 'STUDENT' AND u.username LIKE '2023%';


INSERT IGNORE INTO students (user_id, roll_no, program, year, full_name, email)
SELECT 
    u.user_id,
    u.username as roll_no,
    CASE 
        WHEN (u.user_id % 5) = 0 THEN 'B.Tech Computer Science & Engineering'
        WHEN (u.user_id % 5) = 1 THEN 'B.Tech Electronics & Communication Engineering'
        WHEN (u.user_id % 5) = 2 THEN 'B.Tech Computer Science & Design'
        WHEN (u.user_id % 5) = 3 THEN 'B.Tech Computer Science & Applied Mathematics'
        ELSE 'B.Tech Computer Science & Biosciences'
    END as program,
    1 as year,
    CONCAT('Student ', u.username) as full_name,
    CONCAT(u.username, '@iiitd.ac.in') as email
FROM auth_db.users_auth u
WHERE u.role = 'STUDENT' AND u.username LIKE '2024%';


DROP TEMPORARY TABLE IF EXISTS numbers;


SELECT 
    'Students Created Successfully' as status,
    COUNT(*) as total_students
FROM auth_db.users_auth 
WHERE role = 'STUDENT';


SELECT 
    LEFT(username, 4) as batch,
    COUNT(*) as student_count
FROM auth_db.users_auth 
WHERE role = 'STUDENT'
GROUP BY LEFT(username, 4)
ORDER BY batch;