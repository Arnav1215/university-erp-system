

USE university_erp;


INSERT INTO enrollments (student_id, section_id, status, enrollment_date)
SELECT 
    s.user_id as student_id,
    sec.section_id,
    'ENROLLED' as status,
    NOW() as enrollment_date
FROM students s
CROSS JOIN (
    SELECT section_id, course_id, capacity, enrolled_count
    FROM sections 
    WHERE course_id IN (
        SELECT course_id FROM courses 
        WHERE code IN ('CSE401', 'CSE402', 'CSE403', 'CSE404', 'CSE405', 'ECE401', 'ECE402', 'MTH401', 'SEM101', 'ELE101')
    )
) sec
WHERE s.year = 4 
AND RAND() < 0.4  
AND sec.enrolled_count < sec.capacity
ORDER BY RAND()
LIMIT 200;


INSERT INTO enrollments (student_id, section_id, status, enrollment_date)
SELECT 
    s.user_id as student_id,
    sec.section_id,
    'ENROLLED' as status,
    NOW() as enrollment_date
FROM students s
CROSS JOIN (
    SELECT section_id, course_id, capacity, enrolled_count
    FROM sections 
    WHERE course_id IN (
        SELECT course_id FROM courses 
        WHERE code IN ('CSE301', 'CSE302', 'CSE303', 'CSE304', 'ECE301', 'ECE302', 'MTH301', 'MTH302', 'INT102', 'DES201')
    )
) sec
WHERE s.year = 3 
AND RAND() < 0.5  
AND sec.enrolled_count < sec.capacity
AND NOT EXISTS (
    SELECT 1 FROM enrollments e 
    WHERE e.student_id = s.user_id AND e.section_id = sec.section_id
)
ORDER BY RAND()
LIMIT 300;


INSERT INTO enrollments (student_id, section_id, status, enrollment_date)
SELECT 
    s.user_id as student_id,
    sec.section_id,
    'ENROLLED' as status,
    NOW() as enrollment_date
FROM students s
CROSS JOIN (
    SELECT section_id, course_id, capacity, enrolled_count
    FROM sections 
    WHERE course_id IN (
        SELECT course_id FROM courses 
        WHERE code IN ('CSE201', 'CSE202', 'ECE201', 'ECE202', 'MTH201', 'MTH202', 'PHY201', 'ENG102', 'LAB102', 'SSH101')
    )
) sec
WHERE s.year = 2 
AND RAND() < 0.6  
AND sec.enrolled_count < sec.capacity
AND NOT EXISTS (
    SELECT 1 FROM enrollments e 
    WHERE e.student_id = s.user_id AND e.section_id = sec.section_id
)
ORDER BY RAND()
LIMIT 400;


INSERT INTO enrollments (student_id, section_id, status, enrollment_date)
SELECT 
    s.user_id as student_id,
    sec.section_id,
    'ENROLLED' as status,
    NOW() as enrollment_date
FROM students s
CROSS JOIN (
    SELECT section_id, course_id, capacity, enrolled_count
    FROM sections 
    WHERE course_id IN (
        SELECT course_id FROM courses 
        WHERE code IN ('CSE101', 'CSE102', 'ECE101', 'ECE102', 'MTH101', 'MTH102', 'PHY101', 'PHY102', 'ENG101', 'LAB101')
    )
) sec
WHERE s.year = 1 
AND RAND() < 0.7  
AND sec.enrolled_count < sec.capacity
AND NOT EXISTS (
    SELECT 1 FROM enrollments e 
    WHERE e.student_id = s.user_id AND e.section_id = sec.section_id
)
ORDER BY RAND()
LIMIT 500;


UPDATE sections s
SET enrolled_count = (
    SELECT COUNT(*) 
    FROM enrollments e 
    WHERE e.section_id = s.section_id AND e.status = 'ENROLLED'
);


INSERT INTO grades (enrollment_id, component, score, max_score)
SELECT 
    e.enrollment_id,
    'Quiz' as component,
    ROUND(60 + RAND() * 35, 2) as score,  
    100.00 as max_score
FROM enrollments e
WHERE RAND() < 0.3  
LIMIT 200;

INSERT INTO grades (enrollment_id, component, score, max_score)
SELECT 
    e.enrollment_id,
    'Midterm' as component,
    ROUND(55 + RAND() * 40, 2) as score,  
    100.00 as max_score
FROM enrollments e
WHERE RAND() < 0.2  
LIMIT 150;


UPDATE enrollments 
SET drop_deadline = DATE_ADD(enrollment_date, INTERVAL 30 DAY);


SELECT 
    'Enrollment Statistics' as info,
    COUNT(*) as total_enrollments,
    COUNT(DISTINCT student_id) as students_enrolled,
    COUNT(DISTINCT section_id) as sections_with_students
FROM enrollments;


SELECT 
    s.year,
    COUNT(e.enrollment_id) as total_enrollments,
    COUNT(DISTINCT e.student_id) as unique_students
FROM enrollments e
JOIN students s ON e.student_id = s.user_id
GROUP BY s.year
ORDER BY s.year DESC;


SELECT 
    'Section Utilization' as info,
    AVG(enrolled_count) as avg_enrollment,
    MAX(enrolled_count) as max_enrollment,
    MIN(enrolled_count) as min_enrollment,
    COUNT(*) as total_sections
FROM sections
WHERE enrolled_count > 0;