
USE university_erp;

INSERT INTO courses (code, title, credits, description) VALUES
('CSE101', 'Introduction to Programming', 4, 'Basic programming concepts using Python'),
('CSE102', 'Data Structures and Algorithms', 4, 'Fundamental data structures and algorithmic techniques'),
('CSE201', 'Computer Systems Organization', 4, 'Computer architecture and system programming'),
('CSE202', 'Discrete Mathematics', 4, 'Mathematical foundations for computer science'),
('CSE301', 'Database Management Systems', 4, 'Design and implementation of database systems'),
('CSE302', 'Operating Systems', 4, 'Operating system concepts and implementation'),
('CSE303', 'Computer Networks', 4, 'Network protocols and distributed systems'),
('CSE304', 'Software Engineering', 4, 'Software development methodologies and practices'),
('CSE401', 'Machine Learning', 4, 'Introduction to machine learning algorithms'),
('CSE402', 'Artificial Intelligence', 4, 'AI concepts and problem-solving techniques'),
('CSE403', 'Computer Graphics', 4, 'Graphics programming and visualization'),
('CSE404', 'Cybersecurity', 4, 'Information security and cryptography'),
('CSE405', 'Web Technologies', 4, 'Modern web development frameworks'),
('CSE406', 'Mobile Computing', 4, 'Mobile application development'),
('CSE407', 'Cloud Computing', 4, 'Cloud platforms and distributed computing'),

('ECE101', 'Circuit Analysis', 4, 'Basic electrical circuit analysis'),
('ECE102', 'Digital Logic Design', 4, 'Digital circuits and logic design'),
('ECE201', 'Signals and Systems', 4, 'Signal processing fundamentals'),
('ECE202', 'Electronic Devices', 4, 'Semiconductor devices and circuits'),
('ECE301', 'Communication Systems', 4, 'Analog and digital communication'),
('ECE302', 'Microprocessors', 4, 'Microprocessor architecture and programming'),
('ECE303', 'VLSI Design', 4, 'Very Large Scale Integration design'),
('ECE304', 'Embedded Systems', 4, 'Embedded system design and programming'),
('ECE401', 'Wireless Communication', 4, 'Wireless networks and protocols'),
('ECE402', 'Digital Signal Processing', 4, 'DSP algorithms and applications'),


('MTH101', 'Calculus I', 4, 'Differential and integral calculus'),
('MTH102', 'Linear Algebra', 4, 'Vector spaces and matrix theory'),
('MTH201', 'Calculus II', 4, 'Multivariable calculus'),
('MTH202', 'Probability and Statistics', 4, 'Probability theory and statistical methods'),
('MTH301', 'Numerical Methods', 4, 'Computational mathematics'),
('MTH302', 'Optimization', 4, 'Mathematical optimization techniques'),

('PHY101', 'Physics I', 4, 'Mechanics and thermodynamics'),
('PHY102', 'Physics II', 4, 'Electricity and magnetism'),
('PHY201', 'Modern Physics', 4, 'Quantum mechanics and relativity'),

('CHM101', 'General Chemistry', 4, 'Basic chemical principles'),
('CHM102', 'Organic Chemistry', 4, 'Organic compounds and reactions'),

('BIO101', 'Biology Fundamentals', 4, 'Basic biological concepts'),
('BIO201', 'Computational Biology', 4, 'Bioinformatics and computational methods'),

('ENG101', 'English Communication', 3, 'Academic writing and communication skills'),
('ENG102', 'Technical Writing', 3, 'Technical documentation and presentation'),

('SSH101', 'Introduction to Psychology', 3, 'Basic psychological principles'),
('SSH102', 'Economics', 3, 'Microeconomics and macroeconomics'),
('SSH103', 'Philosophy', 3, 'Introduction to philosophical thinking'),
('SSH104', 'Sociology', 3, 'Social structures and behavior'),
('SSH105', 'Political Science', 3, 'Government and political systems'),

('DES101', 'Design Thinking', 3, 'Creative problem-solving methodology'),
('DES201', 'Human-Computer Interaction', 4, 'User interface design principles'),
('DES301', 'User Experience Design', 4, 'UX research and design methods'),

('ENT101', 'Entrepreneurship Fundamentals', 3, 'Business planning and startup basics'),
('ENT201', 'Innovation Management', 3, 'Managing innovation in organizations'),

('CSE501', 'Advanced Algorithms', 4, 'Complex algorithmic techniques'),
('CSE502', 'Compiler Design', 4, 'Programming language implementation'),
('CSE503', 'Computer Vision', 4, 'Image processing and computer vision'),
('CSE504', 'Natural Language Processing', 4, 'Computational linguistics'),
('CSE505', 'Distributed Systems', 4, 'Large-scale distributed computing'),
('CSE506', 'Blockchain Technology', 4, 'Cryptocurrency and blockchain systems'),
('CSE507', 'Internet of Things', 4, 'IoT systems and applications'),
('CSE508', 'Quantum Computing', 4, 'Quantum algorithms and computing'),
('CSE509', 'Robotics', 4, 'Robot design and control systems'),
('CSE510', 'Game Development', 4, 'Video game programming and design'),

('ECE501', 'RF Engineering', 4, 'Radio frequency circuit design'),
('ECE502', 'Antenna Design', 4, 'Antenna theory and applications'),
('ECE503', 'Power Electronics', 4, 'Power conversion systems'),
('ECE504', 'Control Systems', 4, 'Automatic control theory'),
('ECE505', 'Biomedical Engineering', 4, 'Medical device design'),

('RES101', 'Research Methodology', 3, 'Scientific research methods'),
('RES201', 'Independent Study', 4, 'Self-directed research project'),
('RES301', 'Capstone Project I', 4, 'Major design project - Part 1'),
('RES302', 'Capstone Project II', 4, 'Major design project - Part 2'),

('INT101', 'Computational Thinking', 3, 'Problem-solving with computational methods'),
('INT102', 'Data Science', 4, 'Data analysis and visualization'),
('INT103', 'Digital Humanities', 3, 'Technology in humanities research'),
('INT104', 'Environmental Science', 3, 'Environmental issues and solutions'),
('INT105', 'Ethics in Technology', 3, 'Ethical implications of technology'),

('MTH401', 'Advanced Statistics', 4, 'Statistical modeling and inference'),
('MTH402', 'Graph Theory', 4, 'Mathematical graph structures'),
('MTH403', 'Cryptography', 4, 'Mathematical foundations of cryptography'),
('MTH404', 'Operations Research', 4, 'Optimization in business and industry'),

('LAB101', 'Programming Lab', 2, 'Hands-on programming practice'),
('LAB102', 'Electronics Lab', 2, 'Circuit design and testing'),
('LAB103', 'Physics Lab', 2, 'Experimental physics'),
('LAB104', 'Chemistry Lab', 2, 'Chemical experiments and analysis'),
('LAB105', 'Digital Systems Lab', 2, 'Digital circuit implementation'),

('SEM101', 'Technical Seminar', 2, 'Presentation and discussion of technical topics'),
('SEM102', 'Industry Seminar', 2, 'Industry experts and current trends'),
('SPC101', 'Special Topics in AI', 4, 'Current research in artificial intelligence'),
('SPC102', 'Special Topics in Security', 4, 'Advanced cybersecurity topics'),
('SPC103', 'Special Topics in Networks', 4, 'Emerging networking technologies'),

('ELE101', 'Digital Photography', 3, 'Digital image creation and editing'),
('ELE102', 'Music Technology', 3, 'Audio processing and music software'),
('ELE103', 'Sports Analytics', 3, 'Statistical analysis in sports'),
('ELE104', 'Financial Technology', 3, 'Technology in financial services'),
('ELE105', 'Smart Cities', 3, 'Urban technology and planning'),
('ELE106', 'Renewable Energy', 3, 'Sustainable energy technologies'),
('ELE107', 'Space Technology', 3, 'Satellite and space systems'),
('ELE108', 'Automotive Technology', 3, 'Modern vehicle systems'),
('ELE109', 'Healthcare Technology', 3, 'Medical informatics and devices'),
('ELE110', 'Agricultural Technology', 3, 'Technology in agriculture and farming');

INSERT INTO sections (course_id, day, time, room, capacity, semester, year) 
SELECT 
    c.course_id,
    CASE 
        WHEN s.section_num = 1 THEN 'Monday/Wednesday'
        WHEN s.section_num = 2 THEN 'Tuesday/Thursday'
        ELSE 'Friday'
    END as day,
    CASE 
        WHEN s.section_num = 1 THEN '09:00-10:30'
        WHEN s.section_num = 2 THEN '11:00-12:30'
        ELSE '14:00-15:30'
    END as time,
    CONCAT(
        CASE 
            WHEN c.code LIKE 'CSE%' THEN 'A'
            WHEN c.code LIKE 'ECE%' THEN 'B'
            WHEN c.code LIKE 'MTH%' THEN 'C'
            WHEN c.code LIKE 'PHY%' THEN 'D'
            WHEN c.code LIKE 'LAB%' THEN 'L'
            ELSE 'G'
        END,
        LPAD(FLOOR(RAND() * 99) + 1, 2, '0')
    ) as room,
    CASE 
        WHEN c.code LIKE 'LAB%' THEN 25
        WHEN c.code LIKE 'SEM%' THEN 30
        WHEN c.code LIKE 'CSE5%' OR c.code LIKE 'ECE5%' THEN 35
        ELSE 50
    END as capacity,
    'Fall' as semester,
    2024 as year
FROM courses c
CROSS JOIN (
    SELECT 1 as section_num
    UNION SELECT 2
) s
WHERE c.code IN (
    'CSE101', 'CSE102', 'CSE201', 'CSE202', 'CSE301', 'CSE302', 'CSE303', 'CSE304',
    
    'ECE101', 'ECE102', 'ECE201', 'ECE202',
    
    'MTH101', 'MTH102', 'MTH201', 'MTH202',
    
    'PHY101', 'PHY102', 'PHY201',
    
    'LAB101', 'LAB102', 'LAB103', 'LAB104', 'LAB105',
    
    'ENG101', 'ENG102',
    
    'CSE401', 'CSE402', 'CSE405', 'INT102', 'DES101', 'ENT101'
)
ORDER BY c.course_id, s.section_num;


INSERT INTO sections (course_id, day, time, room, capacity, semester, year)
SELECT 
    c.course_id,
    'Tuesday/Thursday' as day,
    '14:00-15:30' as time,
    CONCAT(
        CASE 
            WHEN c.code LIKE 'CSE%' THEN 'A'
            WHEN c.code LIKE 'ECE%' THEN 'B'
            WHEN c.code LIKE 'SSH%' THEN 'G'
            ELSE 'G'
        END,
        LPAD(FLOOR(RAND() * 99) + 1, 2, '0')
    ) as room,
    40 as capacity,
    'Fall' as semester,
    2024 as year
FROM courses c
WHERE c.code IN (
    'SSH101', 'SSH102', 'CHM101', 'BIO101', 'DES201',
    'CSE403', 'ECE301', 'MTH301', 'SEM101', 'ELE101'
);


INSERT INTO section_assessment_weights (section_id, quiz_weight, midterm_weight, endsem_weight)
SELECT 
    section_id,
    20.00 as quiz_weight,
    30.00 as midterm_weight,
    50.00 as endsem_weight
FROM sections;N 30
        WHEN c.code LIKE 'CSE5%' OR c.code LIKE 'ECE5%' THEN 35
        ELSE 50
    END as capacity,
    'Fall' as semester,
    2024 as year
FROM courses c
CROSS JOIN (
    SELECT 1 as section_num
    UNION SELECT 2
    UNION SELECT 3
) s
WHERE c.code NOT LIKE 'RES%'
ORDER BY c.course_id, s.section_num;


INSERT INTO sections (course_id, day, time, room, capacity, semester, year)
SELECT 
    c.course_id,
    CASE 
        WHEN RAND() < 0.5 THEN 'Monday/Wednesday'
        ELSE 'Tuesday/Thursday'
    END as day,
    CASE 
        WHEN RAND() < 0.33 THEN '09:00-10:30'
        WHEN RAND() < 0.66 THEN '11:00-12:30'
        ELSE '14:00-15:30'
    END as time,
    CONCAT(
        CASE 
            WHEN c.code LIKE 'CSE%' THEN 'A'
            WHEN c.code LIKE 'ECE%' THEN 'B'
            WHEN c.code LIKE 'MTH%' THEN 'C'
            WHEN c.code LIKE 'PHY%' THEN 'D'
            WHEN c.code LIKE 'LAB%' THEN 'L'
            ELSE 'G'
        END,
        LPAD(FLOOR(RAND() * 99) + 1, 2, '0')
    ) as room,
    CASE 
        WHEN c.code LIKE 'LAB%' THEN 25
        WHEN c.code LIKE 'SEM%' THEN 30
        WHEN c.code LIKE 'CSE5%' OR c.code LIKE 'ECE5%' THEN 35
        ELSE 50
    END as capacity,
    'Spring' as semester,
    2025 as year
FROM courses c
WHERE c.code IN (
    'CSE101', 'CSE102', 'CSE201', 'CSE202', 'CSE301', 'CSE302',
    'ECE101', 'ECE102', 'ECE201', 'ECE202',
    'MTH101', 'MTH102', 'MTH201', 'MTH202',
    'PHY101', 'PHY102', 'ENG101', 'ENG102',
    'LAB101', 'LAB102', 'LAB103'
);


INSERT INTO section_assessment_weights (section_id, quiz_weight, midterm_weight, endsem_weight)
SELECT 
    section_id,
    20.00 as quiz_weight,
    30.00 as midterm_weight,
    50.00 as endsem_weight
FROM sections;