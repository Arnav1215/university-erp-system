package edu.univ.erp.ui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.univ.erp.auth.Session;
import edu.univ.erp.domain.User;


public class DashboardTest {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            
            testDashboard("ADMIN");
            testDashboard("INSTRUCTOR"); 
            testDashboard("STUDENT");
        });
    }
    
    private static void testDashboard(String role) {
        
        User testUser = new User();
        testUser.setId(1);
        testUser.setUsername("test_" + role.toLowerCase());
        testUser.setRole(role);
        testUser.setFullName("Test " + role);
        
        
        Session.getInstance().setUser(testUser);
        
        
        JFrame frame = new JFrame("Dashboard Test - " + role);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        
        DashboardHomePanel dashboard = new DashboardHomePanel(role);
        frame.add(dashboard);
        
        frame.setVisible(true);
        
        System.out.println("Dashboard created for role: " + role);
    }
}