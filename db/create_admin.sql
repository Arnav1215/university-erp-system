USE auth_db;
INSERT INTO users_auth (username, role, password_hash, status) VALUES
('admin', 'ADMIN', '$2a$10$OJF5sgoeAy8YmqqI5bcc4.k0rTEwACLOfAcIYhSK84LPbv.YQiqVu', 'ACTIVE')
ON DUPLICATE KEY UPDATE username = VALUES(username);
SELECT user_id, username, role, status FROM users_auth WHERE username = 'admin';

