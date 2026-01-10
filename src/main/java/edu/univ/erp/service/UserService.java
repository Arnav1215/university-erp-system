package edu.univ.erp.service;

import edu.univ.erp.auth.PasswordHash;
import edu.univ.erp.auth.Session;
import edu.univ.erp.dao.UserDAO;
import edu.univ.erp.domain.User;
import edu.univ.erp.exception.BusinessException;
import edu.univ.erp.exception.DataAccessException;
import edu.univ.erp.exception.ValidationException;
import edu.univ.erp.util.Logger;
import edu.univ.erp.util.ValidationUtil;

public class UserService {
    private final UserDAO dao = new UserDAO();
    private static final Logger logger = Logger.getLogger(UserService.class);

    public User login(String username, String password) {
        try {
            ValidationUtil.validateNotEmpty(username, "Username");
            ValidationUtil.validateNotEmpty(password, "Password");
            
            User u = dao.findByUsername(username.trim());
            if (u == null) {
                logger.warn("Login attempt with non-existent username: {}", username);
            return null;
        }
        
        // Verify password hash
        if (PasswordHash.verify(password, u.getPassword())) {
            dao.updateLastLogin(u.getId());
                logger.info("Successful login for user: {}", username);
            return u;
            } else {
                logger.warn("Failed login attempt for user: {} (incorrect password)", username);
            }
        } catch (ValidationException e) {
            logger.warn("Login validation error: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error during login: {}", username, e);
            throw new BusinessException("Unable to process login. Please try again.");
        }
        return null;
    }

    public boolean createUser(User user, String plainPassword) {
        try {
            ValidationUtil.validateNotNull(user, "User");
            ValidationUtil.validateUsername(user.getUsername());
            ValidationUtil.validatePassword(plainPassword);
            ValidationUtil.validateRole(user.getRole());
            
           
            if (dao.exists(user.getUsername())) {
                throw new BusinessException("Username already exists: " + user.getUsername());
        }
            
        String hash = PasswordHash.hash(plainPassword);
            boolean created = dao.create(user, hash);
            if (created) {
                logger.info("User created successfully: {}", user.getUsername());
            }
            return created;
        } catch (ValidationException | BusinessException e) {
            logger.warn("User creation validation error: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error during user creation: {}", user != null ? user.getUsername() : "null", e);
            throw new BusinessException("Unable to create user. Please try again.");
        }
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        try {
            ValidationUtil.validateNotEmpty(oldPassword, "Old password");
            ValidationUtil.validatePassword(newPassword);
            
            User currentUser = Session.getInstance().getCurrentUser();
            if (currentUser == null) {
                throw new BusinessException("No user session found");
            }
            
            User user = dao.findByUsername(currentUser.getUsername());
            if (user == null) {
                throw new BusinessException("User not found");
            }
            
            if (!PasswordHash.verify(oldPassword, user.getPassword())) {
                logger.warn("Password change failed: incorrect old password for user: {}", user.getUsername());
                throw new BusinessException("Incorrect old password");
            }
            
            if (oldPassword.equals(newPassword)) {
                throw new BusinessException("New password must be different from old password");
        }
            
        String newHash = PasswordHash.hash(newPassword);
            boolean updated = dao.updatePassword(userId, newHash);
            if (updated) {
                logger.info("Password changed successfully for user: {}", user.getUsername());
            }
            return updated;
        } catch (ValidationException | BusinessException e) {
            logger.warn("Password change validation error: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error during password change for user: {}", userId, e);
            throw new BusinessException("Unable to change password. Please try again.");
        }
    }
}

