package edu.univ.erp.ui;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.univ.erp.ui.components.MaintenanceModeNotifier;
import edu.univ.erp.ui.components.MaintenanceWrapper;

public class InstructorDashboard extends JFrame {

    private JPanel contentPanel;
    private JPanel displayedPanel;
    private final Runnable maintenanceListener = this::refreshMaintenanceBanner;

    public InstructorDashboard() {
        setTitle("Instructor Dashboard | University ERP");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        SidebarPanel sidebar = new SidebarPanel("Instructor", this, this::logout);
        add(sidebar, BorderLayout.WEST);

        setContentPanel(new DashboardHomePanel("Instructor"));

        MaintenanceModeNotifier.addListener(maintenanceListener);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                MaintenanceModeNotifier.removeListener(maintenanceListener);
            }
        });
    }

    public void setContentPanel(JPanel panel) {
        contentPanel = panel;
        refreshMaintenanceBanner();
    }

    private void logout() {
        MaintenanceModeNotifier.removeListener(maintenanceListener);
        edu.univ.erp.api.auth.AuthAPI authAPI = new edu.univ.erp.api.auth.AuthAPI();
        authAPI.logout();
        new LoginForm().setVisible(true);
        dispose();
    }

    private void refreshMaintenanceBanner() {
        SwingUtilities.invokeLater(() -> {
            if (contentPanel == null) {
                return;
            }
            if (displayedPanel != null) {
                getContentPane().remove(displayedPanel);
            }
            displayedPanel = MaintenanceWrapper.wrapWithBanner(contentPanel);
            add(displayedPanel, BorderLayout.CENTER);
            revalidate();
            repaint();
        });
    }
}
