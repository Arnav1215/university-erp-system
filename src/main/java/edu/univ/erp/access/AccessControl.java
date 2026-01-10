package edu.univ.erp.access;

import edu.univ.erp.auth.Session;
import edu.univ.erp.dao.SettingsDAO;

public class AccessControl {
    private static final SettingsDAO settingsDAO = new SettingsDAO();

    public static boolean isMaintenanceMode() {
        return settingsDAO.isMaintenanceMode();
    }

    public static boolean canModify() {
        Session session = Session.getInstance();
        
        if (session.isAdmin()) {
            return true;
        }
        
        return !isMaintenanceMode();
    }

    public static boolean canAccessAsAdmin() {
        return Session.getInstance().isAdmin();
    }

    public static boolean canAccessAsInstructor(int instructorId) {
        Session session = Session.getInstance();
        if (session.isAdmin()) return true;
        return session.isInstructor() && session.getUserId() == instructorId;
    }

    public static boolean canAccessAsStudent(int studentId) {
        Session session = Session.getInstance();
        if (session.isAdmin()) return true;
        return session.isStudent() && session.getUserId() == studentId;
    }

    public static String getAccessDeniedMessage() {
        if (isMaintenanceMode()) {
            return "System is in maintenance mode. Changes are not allowed.";
        }
        return "You do not have permission to perform this action.";
    }
}

