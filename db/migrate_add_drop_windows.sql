

USE university_erp;



SET @dbname = DATABASE();
SET @tablename = 'sections';
SET @columnname = 'created_at';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (TABLE_SCHEMA = @dbname)
      AND (TABLE_NAME = @tablename)
      AND (COLUMN_NAME = @columnname)
  ) > 0,
  'SELECT 1', 
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' TIMESTAMP DEFAULT CURRENT_TIMESTAMP')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;



UPDATE sections 
SET created_at = CURRENT_TIMESTAMP 
WHERE created_at IS NULL;



SET @columnname2 = 'drop_deadline';
SET @preparedStatement2 = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (TABLE_SCHEMA = @dbname)
      AND (TABLE_NAME = 'enrollments')
      AND (COLUMN_NAME = @columnname2)
  ) > 0,
  'ALTER TABLE enrollments MODIFY COLUMN drop_deadline TIMESTAMP NULL', 
  'SELECT 1' 
));
PREPARE alterIfExists FROM @preparedStatement2;
EXECUTE alterIfExists;
DEALLOCATE PREPARE alterIfExists;



UPDATE enrollments 
SET drop_deadline = DATE_ADD(enrollment_date, INTERVAL 7 DAY)
WHERE drop_deadline IS NULL 
  AND enrollment_date IS NOT NULL 
  AND status = 'ENROLLED';


SELECT 
    COUNT(*) as total_sections,
    SUM(CASE WHEN created_at IS NULL THEN 1 ELSE 0 END) as sections_without_created_at
FROM sections;

SELECT 
    COUNT(*) as total_enrollments,
    SUM(CASE WHEN drop_deadline IS NULL THEN 1 ELSE 0 END) as enrollments_without_drop_deadline
FROM enrollments 
WHERE status = 'ENROLLED';

