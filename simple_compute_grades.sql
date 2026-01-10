
USE university_erp;


ALTER TABLE enrollments 
ADD COLUMN IF NOT EXISTS final_grade VARCHAR(5),
ADD COLUMN IF NOT EXISTS gpa_points DECIMAL(4,2);


ALTER TABLE students 
ADD COLUMN IF NOT EXISTS sgpa DECIMAL(4,2),
ADD COLUMN IF NOT EXISTS cgpa DECIMAL(4,2);


UPDATE enrollments e
SET 
    final_grade = (
        SELECT 
            CASE 
                WHEN AVG(g.score) >= 90 THEN 'A+'
                WHEN AVG(g.score) >= 80 THEN 'A'
                WHEN AVG(g.score) >= 70 THEN 'B+'
                WHEN AVG(g.score) >= 60 THEN 'B'
                WHEN AVG(g.score) >= 50 THEN 'C+'
                WHEN AVG(g.score) >= 40 THEN 'C'
                WHEN AVG(g.score) >= 35 THEN 'D'
                ELSE 'F'
            END
        FROM grades g 
        WHERE g.enrollment_id = e.enrollment_id
    ),
    gpa_points = (
        SELECT 
            CASE 
                WHEN AVG(g.score) >= 90 THEN 10.0
                WHEN AVG(g.score) >= 80 THEN 9.0
                WHEN AVG(g.score) >= 70 THEN 8.0
                WHEN AVG(g.score) >= 60 THEN 7.0
                WHEN AVG(g.score) >= 50 THEN 6.0
                WHEN AVG(g.score) >= 40 THEN 5.0
                WHEN AVG(g.score) >= 35 THEN 4.0
                ELSE 0.0
            END
        FROM grades g 
        WHERE g.enrollment_id = e.enrollment_id
    )
WHERE EXISTS (SELECT 1 FROM grades g WHERE g.enrollment_id = e.enrollment_id);


UPDATE students s
SET sgpa = (
    SELECT ROUND(AVG(e.gpa_points), 2)
    FROM enrollments e
    WHERE e.student_id = s.user_id 
    AND e.gpa_points IS NOT NULL
);


UPDATE students s
SET cgpa = sgpa; 

SELECT 'Grades computed with 0-10 CGPA/SGPA scale' as result;