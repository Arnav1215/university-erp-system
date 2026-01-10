package edu.univ.erp.ui.components;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import edu.univ.erp.access.AccessControl;
public final class MaintenanceWrapper {

    private MaintenanceWrapper() {
    }

    public static JPanel wrapWithBanner(JPanel panel) {
        if (!AccessControl.isMaintenanceMode()) {
            return panel;
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(panel.isOpaque());
        wrapper.setBackground(panel.getBackground());
        wrapper.add(new MaintenanceBanner(), BorderLayout.NORTH);
        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }
}

