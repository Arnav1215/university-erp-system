package edu.univ.erp.ui;

import java.awt.Cursor;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import edu.univ.erp.ui.panels.AddCoursePanel;
import edu.univ.erp.ui.panels.AddSectionPanel;
import edu.univ.erp.ui.panels.AddUserPanel;
import edu.univ.erp.ui.panels.EditCoursesPanel;
import edu.univ.erp.ui.panels.EditSectionsPanel;
import edu.univ.erp.ui.panels.AssessmentSetupPanel;
import edu.univ.erp.ui.panels.ComputeGradesPanel;
import edu.univ.erp.ui.panels.EnterScoresPanel;
import edu.univ.erp.ui.panels.MaintenancePanel;
import edu.univ.erp.ui.panels.MySectionsPanel;
import edu.univ.erp.ui.panels.SectionManagementPanel;
import edu.univ.erp.ui.panels.ClassStatsPanel;
import edu.univ.erp.ui.panels.StudentCoursesPanel;
import edu.univ.erp.ui.panels.StudentGradesPanel;
import edu.univ.erp.ui.panels.StudentMyRegistrationsPanel;
import edu.univ.erp.ui.panels.StudentProfilePanel;
import edu.univ.erp.ui.panels.StudentRegisterPanel;
import edu.univ.erp.ui.panels.StudentTimetablePanel;
import edu.univ.erp.ui.panels.StudentTranscriptPanel;
import edu.univ.erp.ui.theme.ERPFonts;

public class SidebarMenuBuilder {

    public static void addRoleMenus(
            String role,
            JPanel menuPanel,
            JFrame parentFrame,
            Consumer<JButton> highlightAction
    ) {
        if (role.equalsIgnoreCase("Admin")) {

            menuPanel.add(createNav("👤 Add Users",
                    parentFrame, new AddUserPanel(), highlightAction));

            menuPanel.add(createNav("📘 Add Courses",
                    parentFrame, new AddCoursePanel(), highlightAction));

            menuPanel.add(createNav("📚 Add Sections",
                    parentFrame, new AddSectionPanel(), highlightAction));

            menuPanel.add(createNav("✏️ Edit Courses",
                    parentFrame, new EditCoursesPanel(), highlightAction));

            menuPanel.add(createNav("📝 Edit Sections",
                    parentFrame, new EditSectionsPanel(), highlightAction));

            menuPanel.add(createNav("👥 Manage Sections",
                    parentFrame, new SectionManagementPanel(), highlightAction));

            menuPanel.add(createNav("⚙️ Maintenance",
                    parentFrame, new MaintenancePanel(), highlightAction));

            menuPanel.add(createChangePasswordNav(parentFrame, highlightAction));
        }  
        else if (role.equalsIgnoreCase("Instructor")) {

            menuPanel.add(createNav("📚 My Sections",
                    parentFrame, new MySectionsPanel(), highlightAction));

            menuPanel.add(createNav("📝 Enter Scores",
                    parentFrame, new EnterScoresPanel(), highlightAction));

            menuPanel.add(createNav("⚖ Assessment Setup",
                    parentFrame, new AssessmentSetupPanel(), highlightAction));

            menuPanel.add(createNav("🧮 Compute Grades",
                    parentFrame, new ComputeGradesPanel(), highlightAction));

            menuPanel.add(createNav("📈 Class Stats",
                    parentFrame, new ClassStatsPanel(), highlightAction));

            menuPanel.add(createChangePasswordNav(parentFrame, highlightAction));
        }
        else {

            menuPanel.add(createNav("📘 My Courses",
                    parentFrame, new StudentCoursesPanel(), highlightAction));

            menuPanel.add(createNav("📝 Register",
                    parentFrame, new StudentRegisterPanel(), highlightAction));

            menuPanel.add(createNav("📚 My Registrations",
                    parentFrame, new StudentMyRegistrationsPanel(), highlightAction));

            menuPanel.add(createNav("🗓 Timetable",
                    parentFrame, new StudentTimetablePanel(), highlightAction));

            menuPanel.add(createNav("📊 Grades",
                    parentFrame, new StudentGradesPanel(), highlightAction));

            menuPanel.add(createNav("📄 Transcript",
                    parentFrame, new StudentTranscriptPanel(), highlightAction));

            menuPanel.add(createNav("👤 Profile",
                    parentFrame, new StudentProfilePanel(), highlightAction));

            menuPanel.add(createChangePasswordNav(parentFrame, highlightAction));
        }
    }

    
    
    
    private static JButton createNav(
            String text,
            JFrame frame,
            JPanel panel,
            Consumer<JButton> highlightAction
    ) {

        JButton btn = new JButton(text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(ERPFonts.NORMAL);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 10));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            highlightAction.accept(btn);

            if (frame instanceof AdminDashboard a) a.setContentPanel(panel);
            else if (frame instanceof StudentDashboard s) s.setContentPanel(panel);
            else if (frame instanceof InstructorDashboard i) i.setContentPanel(panel);
        });

        return btn;
    }

    private static JButton createChangePasswordNav(JFrame frame, Consumer<JButton> highlightAction) {
        JButton btn = new JButton("🔐 Change Password");
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(ERPFonts.NORMAL);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 10));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            highlightAction.accept(btn);
            new ChangePasswordDialog(frame).setVisible(true);
        });

        return btn;
    }
}
