USE university_erp;
INSERT INTO grades (enrollment_id, component, score, max_score)
SELECT 
    e.enrollment_id,
    'Quiz' as component,
    ROUND(65 + RAND() * 30, 2) as score,  
    100.00 as max_score
FROM enrollments e
WHERE NOT EXISTS (
    SELECT 1 FROM grades g 
    WHERE g.enrollment_id = e.enrollment_id AND g.component = 'Quiz'
);

INSERT INTO grades (enrollment_id, component, score, max_score)
SELECT 
    e.enrollment_id,
    'Midterm' as component,
    ROUND(60 + RAND() * 35, 2) as score,  
    100.00 as max_score
FROM enrollments e
WHERE NOT EXISTS (
    SELECT 1 FROM grades g 
    WHERE g.enrollment_id = e.enrollment_id AND g.component = 'Midterm'
);


INSERT INTO grades (enrollment_id, component, score, max_score)
SELECT 
    e.enrollment_id,
    'Final' as component,
    ROUND(55 + RAND() * 40, 2) as score, 
    100.00 as max_score
FROM enrollments e
WHERE NOT EXISTS (
    SELECT 1 FROM grades g 
    WHERE g.enrollment_id = e.enrollment_id AND g.component = 'Final'
);

INSERT INTO grades (enrollment_id, component, score, max_score)
SELECT 
    e.enrollment_id,
    'Assignment' as component,
    ROUND(70 + RAND() * 25, 2) as score,  
    100.00 as max_score
FROM enrollments e
WHERE NOT EXISTS (
    SELECT 1 FROM grades g 
    WHERE g.enrollment_id = e.enrollment_id AND g.component = 'Assignment'
);

SELECT 
    'Marks Assignment Complete' as status,
    COUNT(*) as total_grades,
    COUNT(DISTINCT enrollment_id) as students_with_grades
FROM grades;



SELECT 
    component,
    COUNT(*) as total_grades,
    ROUND(AVG(score), 2) as avg_score,
    ROUND(MIN(score), 2) as min_score,
    ROUND(MAX(score), 2) as max_score
FROM grades
GROUP BY component
ORDER BY component;