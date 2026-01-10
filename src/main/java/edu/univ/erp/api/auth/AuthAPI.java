package edu.univ.erp.api.auth;

import edu.univ.erp.auth.Session;
import edu.univ.erp.dao.InstructorDAO;
import edu.univ.erp.dao.StudentDAO;
import edu.univ.erp.domain.User;
import edu.univ.erp.exception.BusinessException;
import edu.univ.erp.exception.ValidationException;
import edu.univ.erp.service.UserService;
import edu.univ.erp.util.Logger;

public class AuthAPI {
    private final UserService userService = new UserService();
    private final StudentDAO studentDAO = new StudentDAO();
    private final InstructorDAO instructorDAO = new InstructorDAO();
    private static final Logger logger = Logger.getLogger(AuthAPI.class);

    public LoginResult login(String username, String password) {
        try {
            User user = userService.login(username, password);
            if (user != null) {
                Session.getInstance().setUser(user);
                
                
                try {
                    if ("STUDENT".equalsIgnoreCase(user.getRole())) {
                        Session.getInstance().setStudentProfile(studentDAO.findByUserId(user.getId()));
                    } else if ("INSTRUCTOR".equalsIgnoreCase(user.getRole())) {
                        Session.getInstance().setInstructorProfile(instructorDAO.findByUserId(user.getId()));
                    }
                } catch (Exception e) {
                    logger.warn("Failed to load profile for user: {}", user.getUsername(), e);
                    
                }
                
                return new LoginResult(true, "Login successful", user);
            }
            return new LoginResult(false, "Incorrect username or password", null);
        } catch (ValidationException e) {
            logger.warn("Login validation error: {}", e.getMessage());
            return new LoginResult(false, e.getMessage(), null);
        } catch (BusinessException e) {
            logger.error("Login business error: {}", e.getMessage());
            return new LoginResult(false, "Unable to process login. Please try again.", null);
        } catch (Exception e) {
            logger.error("Unexpected error during login", e);
            return new LoginResult(false, "An unexpected error occurred. Please try again.", null);
        }
    }

    public void logout() {
        try {
            Session.getInstance().clear();
            logger.info("User logged out");
        } catch (Exception e) {
            logger.error("Error during logout", e);
        }
    }

    public User getCurrentUser() {
        try {
            return Session.getInstance().getCurrentUser();
        } catch (Exception e) {
            logger.error("Error getting current user", e);
            return null;
        }
    }

    public String getCurrentRole() {
        try {
            return Session.getInstance().getRole();
        } catch (Exception e) {
            logger.error("Error getting current role", e);
            return null;
        }
    }

    public boolean changePassword(String oldPassword, String newPassword) {
        try {
            Session session = Session.getInstance();
            if (session.getCurrentUser() == null) {
                logger.warn("Password change attempted without active session");
                return false;
            }
            return userService.changePassword(session.getUserId(), oldPassword, newPassword);
        } catch (ValidationException | BusinessException e) {
            logger.warn("Password change error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during password change", e);
            throw new BusinessException("Unable to change password. Please try again.");
        }
    }

    public static class LoginResult {
        private final boolean success;
        private final String message;
        private final User user;

        public LoginResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public User getUser() { return user; }
    }
}

