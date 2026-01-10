package edu.univ.erp.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import edu.univ.erp.ui.theme.ERPColors;
import edu.univ.erp.ui.theme.ERPFonts;

public class SidebarPanel extends JPanel {

    private JButton activeButton = null;

    public SidebarPanel(String role, JFrame parentFrame, Runnable logoutAction) {
        setLayout(new BorderLayout());
        setBackground(ERPColors.SIDEBAR_BG);
        setPreferredSize(new Dimension(220, 0));

        JLabel header = new JLabel("🎓 University ERP", SwingConstants.CENTER);
        header.setFont(ERPFonts.SUBTITLE);
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(header, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(0, 1, 0, 6));
        menuPanel.setBackground(ERPColors.SIDEBAR_BG);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel roleLabel = new JLabel("👤 " + role, SwingConstants.CENTER);
        roleLabel.setForeground(Color.LIGHT_GRAY);
        roleLabel.setFont(ERPFonts.NORMAL);
        roleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 12, 0));
        menuPanel.add(roleLabel);

        JButton homeBtn = createSidebarButton("🏠 Dashboard");
        homeBtn.addActionListener(e -> { highlightButton(homeBtn); setHomePanel(parentFrame, role); });
        menuPanel.add(homeBtn);

        SidebarMenuBuilder.addRoleMenus(role, menuPanel, parentFrame, this::highlightButton);
        add(new JScrollPane(menuPanel), BorderLayout.CENTER);

        JButton logout = new JButton("🚪 Logout");
        logout.setFont(ERPFonts.NORMAL);
        logout.setBackground(new Color(190, 60, 60));
        logout.setForeground(Color.WHITE);
        logout.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        logout.setFocusPainted(false);
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.addActionListener(e -> logoutAction.run());
        add(logout, BorderLayout.SOUTH);
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(ERPFonts.NORMAL);
        btn.setBackground(ERPColors.SIDEBAR_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 10));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn != activeButton)
                    btn.setBackground(ERPColors.SIDEBAR_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn != activeButton)
                    btn.setBackground(ERPColors.SIDEBAR_BUTTON);
            }
        });

        return btn;
    }

    
    
    
    private void highlightButton(JButton btn) {

        
        if (activeButton != null) {
            activeButton.setBackground(new Color(220, 220, 220));  
        }

        
        activeButton = btn;
        activeButton.setBackground(ERPColors.SIDEBAR_ACTIVE);
    }

    private void setHomePanel(JFrame frame, String role) {
        JPanel p = new DashboardHomePanel(role);
        if (frame instanceof AdminDashboard a) a.setContentPanel(p);
        else if (frame instanceof StudentDashboard s) s.setContentPanel(p);
        else if (frame instanceof InstructorDashboard i) i.setContentPanel(p);
    }

    
    static class JPanelPlaceholder extends JPanel {
        public JPanelPlaceholder(String title) {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            JLabel l = new JLabel(title + " (Coming Soon)", SwingConstants.CENTER);
            l.setFont(ERPFonts.SUBTITLE);
            add(l, BorderLayout.CENTER);
        }
    }
}