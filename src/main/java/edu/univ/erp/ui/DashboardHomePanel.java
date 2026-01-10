package edu.univ.erp.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Cursor;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import edu.univ.erp.api.admin.AdminAPI;
import edu.univ.erp.api.instructor.InstructorAPI;
import edu.univ.erp.api.student.StudentAPI;
import edu.univ.erp.auth.Session;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.User;
import edu.univ.erp.ui.theme.ERPColors;
import edu.univ.erp.ui.theme.ERPComponents;
import edu.univ.erp.ui.theme.ERPFonts;

public class DashboardHomePanel extends JPanel {

    private String role;
    private JPanel statsGrid;
    private Session session;
    private JTabbedPane tabs;

    public DashboardHomePanel(String role) {
        this.role = role;
        this.session = Session.getInstance();
        setLayout(new BorderLayout());
        setBackground(ERPColors.BACKGROUND);

        add(makeHeader(), BorderLayout.NORTH);
        tabs = makeTabs();
        add(tabs, BorderLayout.CENTER);

        refreshStats();
    }

    private JPanel makeHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ERPColors.BACKGROUND);
        header.setBorder(new EmptyBorder(20, 20, 10, 20));

        User currentUser = session.getCurrentUser();
        String userName = currentUser != null ? currentUser.getUsername() : "User";
        JLabel title = new JLabel("Welcome, " + userName + " (" + role + ")!");
        title.setFont(ERPFonts.TITLE);
        title.setForeground(ERPColors.TEXT_PRIMARY);

        JButton refresh = new JButton("🔄 Refresh");
        refresh.setBackground(ERPColors.PRIMARY);
        refresh.setForeground(Color.WHITE);
        refresh.setBorder(new EmptyBorder(8, 15, 8, 15));
        refresh.addActionListener(e -> refreshStats());

        header.add(title, BorderLayout.WEST);
        header.add(refresh, BorderLayout.EAST);

        return header;
    }

    private JTabbedPane makeTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(ERPFonts.NORMAL);
        tabs.setBorder(new EmptyBorder(10, 10, 10, 10));

        
        JPanel overview = new JPanel(new BorderLayout(15, 15));
        overview.setBackground(ERPColors.BACKGROUND);
        overview.setBorder(new EmptyBorder(15, 15, 15, 15));

        
        statsGrid = new JPanel();
        statsGrid.setBackground(ERPColors.BACKGROUND);
        statsGrid.setLayout(new GridLayout(1, 3, 20, 20));
        statsGrid.setBorder(new EmptyBorder(0, 0, 20, 0));

        
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        centerPanel.setBackground(ERPColors.BACKGROUND);
        
        centerPanel.add(makeActivityFeed());
        centerPanel.add(makeQuickActions());

        overview.add(statsGrid, BorderLayout.NORTH);
        overview.add(centerPanel, BorderLayout.CENTER);

        tabs.addTab("📊 Overview", overview);

        
        if (session.isAdmin()) {
            tabs.addTab("📚 Courses", makeCoursesPanel());
            tabs.addTab("👥 Users", makeUsersPanel());
        } else if (session.isInstructor()) {
            tabs.addTab("📝 My Sections", makeSectionsPanel());
            tabs.addTab("🎯 Grades", makeGradesPanel());
            tabs.addTab("📊 Analytics", makeAnalyticsPanel());
        } else if (session.isStudent()) {
            tabs.addTab("📚 My Courses", makeMyCoursesPanel());
            tabs.addTab("📝 Registration", makeRegistrationPanel());
            tabs.addTab("🎓 Transcript", makeTranscriptPanel());
        }

        return tabs;
    }

    private JPanel makeQuickActions() {
        JPanel actions = new JPanel();
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        actions.setBackground(ERPColors.BACKGROUND);

        JLabel actionsTitle = new JLabel("⚡ Quick Actions");
        actionsTitle.setFont(ERPFonts.SUBTITLE);
        actionsTitle.setForeground(ERPColors.TEXT_PRIMARY);
        actionsTitle.setBorder(new EmptyBorder(0, 10, 15, 10));
        actions.add(actionsTitle);

        if (session.isAdmin()) {
            actions.add(makeActionButton("📚 Add New Course", "Create courses and manage curriculum", this::openAddCourse));
            actions.add(makeActionButton("👥 Add New User", "Add students, instructors, and staff", this::openAddUser));
            actions.add(makeActionButton("📚 Add Section", "Create new course sections", this::openAddSection));
            actions.add(makeActionButton("📚 Manage Sections", "View and manage all course sections", this::openManageSections));
        } else if (session.isInstructor()) {
            actions.add(makeActionButton("📝 Enter Grades", "Submit student grades and assessments", this::openEnterGrades));
            actions.add(makeActionButton("👥 My Sections", "Manage section enrollments", () -> switchToTab(1)));
            actions.add(makeActionButton("🎯 Setup Assessment", "Configure grade weights", this::openAssessmentSetup));
            actions.add(makeActionButton("📊 Compute Grades", "Calculate final grades", this::openComputeGrades));
        } else if (session.isStudent()) {
            actions.add(makeActionButton("📚 Register Courses", "Enroll in available sections", () -> switchToTab(2)));
            actions.add(makeActionButton("📊 View Grades", "Check current grades and progress", this::openStudentGrades));
            actions.add(makeActionButton("📅 My Schedule", "View class timetable and locations", this::openMySchedule));
            actions.add(makeActionButton("🎓 Transcript", "Generate official transcript", () -> switchToTab(3)));
        }

        return actions;
    }

    private JPanel makeActionButton(String title, String description, Runnable action) {
        JPanel button = new JPanel(new BorderLayout());
        button.setBackground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ERPColors.CARD_BORDER, 1),
            new EmptyBorder(15, 18, 15, 18)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(ERPFonts.NORMAL);
        titleLabel.setForeground(ERPColors.TEXT_PRIMARY);

        JLabel descLabel = new JLabel("<html><small>" + description + "</small></html>");
        descLabel.setFont(ERPFonts.SMALL);
        descLabel.setForeground(ERPColors.TEXT_SECONDARY);

        button.add(titleLabel, BorderLayout.NORTH);
        button.add(descLabel, BorderLayout.CENTER);

        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (action != null) {
                    button.setBackground(ERPColors.PRIMARY);
                    titleLabel.setForeground(Color.WHITE);
                    Timer timer = new Timer(100, e -> {
                        button.setBackground(ERPColors.PRIMARY_LIGHT);
                        titleLabel.setForeground(ERPColors.TEXT_PRIMARY);
                        action.run();
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(ERPColors.PRIMARY_LIGHT);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ERPColors.PRIMARY, 2),
                    new EmptyBorder(14, 17, 14, 17)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ERPColors.CARD_BORDER, 1),
                    new EmptyBorder(15, 18, 15, 18)
                ));
            }
        });

        return button;
    }

    private JPanel makeActivityFeed() {
        JPanel feed = new JPanel();
        feed.setLayout(new BoxLayout(feed, BoxLayout.Y_AXIS));
        feed.setBackground(ERPColors.BACKGROUND);
        feed.setBorder(new EmptyBorder(10, 20, 20, 20));

        JLabel feedTitle = new JLabel("📰 Recent Activity");
        feedTitle.setFont(ERPFonts.SUBTITLE);
        feedTitle.setForeground(ERPColors.TEXT_PRIMARY);
        feedTitle.setBorder(new EmptyBorder(0, 10, 15, 10));
        feed.add(feedTitle);

        
        try {
            if (session.isAdmin()) {
                AdminAPI adminAPI = new AdminAPI();
                int totalUsers = adminAPI.getAllStudents().size() + adminAPI.getAllInstructors().size();
                feed.add(makeActivity("✅ System operational - " + totalUsers + " active users"));
                feed.add(makeActivity("📊 " + adminAPI.getAllCourses().size() + " courses available"));
                feed.add(makeActivity("📚 " + adminAPI.getAllSections().size() + " sections scheduled"));
                feed.add(makeActivity("🔄 Database connection: Active"));
            } else if (session.isInstructor()) {
                InstructorAPI instructorAPI = new InstructorAPI();
                var sections = instructorAPI.getMySections();
                int totalStudents = sections.stream().mapToInt(Section::getEnrolledCount).sum();
                feed.add(makeActivity("📝 Teaching " + sections.size() + " sections"));
                feed.add(makeActivity("👥 " + totalStudents + " students enrolled"));
                feed.add(makeActivity("📅 Current semester: " + getCurrentSemester()));
                feed.add(makeActivity("🎯 Grade submission available"));
            } else if (session.isStudent()) {
                StudentAPI studentAPI = new StudentAPI();
                var enrollments = studentAPI.getMyEnrollments();
                var grades = studentAPI.getMyGrades();
                feed.add(makeActivity("📚 Enrolled in " + enrollments.size() + " courses"));
                feed.add(makeActivity("📊 " + grades.size() + " grades recorded"));
                feed.add(makeActivity("📅 Semester: " + getCurrentSemester()));
                feed.add(makeActivity("🎓 Academic progress tracking active"));
            }
        } catch (Exception e) {
            feed.add(makeActivity("⚠️ Unable to load recent activity"));
            feed.add(makeActivity("🔄 Try refreshing the dashboard"));
        }

        return feed;
    }

    private JPanel makeActivity(String text) {
        JPanel item = ERPComponents.createCard();
        item.setLayout(new BorderLayout());
        item.setBorder(new EmptyBorder(8, 12, 8, 12));
        
        JLabel l = new JLabel("• " + text);
        l.setFont(ERPFonts.SMALL);
        l.setForeground(ERPColors.TEXT_SECONDARY);
        
        item.add(l, BorderLayout.CENTER);
        return item;
    }

    private void refreshStats() {
        
        statsGrid.removeAll();
        statsGrid.add(makeStatCard("🔄 Loading", "Please wait..."));
        statsGrid.revalidate();
        statsGrid.repaint();

        
        SwingUtilities.invokeLater(() -> {
            try {
                Map<String, String> stats = fetchStats();
                
                
                statsGrid.removeAll();
                for (var entry : stats.entrySet()) {
                    statsGrid.add(makeStatCard(entry.getKey(), entry.getValue()));
                }
                
                statsGrid.revalidate();
                statsGrid.repaint();
                
            } catch (Exception e) {
                
                statsGrid.removeAll();
                statsGrid.add(makeStatCard("❌ Error", "Refresh Failed"));
                statsGrid.add(makeStatCard("🔄 Try Again", "Click Refresh"));
                statsGrid.revalidate();
                statsGrid.repaint();
            }
        });
    }

    private JPanel makeStatCard(String title, String value) {
        JPanel card = ERPComponents.createCard();
        card.setLayout(new BorderLayout());

        JLabel h = new JLabel(title);
        h.setFont(ERPFonts.SUBTITLE);
        h.setBorder(new EmptyBorder(10, 10, 5, 10));

        JLabel v = new JLabel(value, SwingConstants.CENTER);
        v.setFont(new Font("Segoe UI", Font.BOLD, 26));
        v.setForeground(new Color(50, 130, 230));
        v.setBorder(new EmptyBorder(0, 10, 10, 10));

        card.add(h, BorderLayout.NORTH);
        card.add(v, BorderLayout.CENTER);

        return card;
    }

    private Map<String, String> fetchStats() {
        try {
            if (session.isAdmin()) {
                AdminAPI adminAPI = new AdminAPI();
                return Map.of(
                        "📘 Total Courses", String.valueOf(adminAPI.getAllCourses().size()),
                        "📚 Total Sections", String.valueOf(adminAPI.getAllSections().size()),
                        "👥 Total Students", String.valueOf(adminAPI.getAllStudents().size()),
                        "👤 Total Instructors", String.valueOf(adminAPI.getAllInstructors().size())
                );
            }

            if (session.isInstructor()) {
                InstructorAPI instructorAPI = new InstructorAPI();
                List<Section> sections = instructorAPI.getMySections();
                int totalStudents = sections.stream().mapToInt(Section::getEnrolledCount).sum();
                int totalSeats = sections.stream().mapToInt(Section::getSeatsLeft).sum();
                
                return Map.of(
                        "📚 My Sections", String.valueOf(sections.size()),
                        "👥 Total Students", String.valueOf(totalStudents),
                        "🪑 Available Seats", String.valueOf(totalSeats)
                );
            }

            if (session.isStudent()) {
                StudentAPI studentAPI = new StudentAPI();
                List<Enrollment> enrollments = studentAPI.getMyEnrollments();
                List<Grade> grades = studentAPI.getMyGrades();
                
                long completedCourses = grades.stream()
                        .filter(g -> g.getFinalGrade() != null)
                        .map(Grade::getEnrollmentId)
                        .distinct()
                        .count();

                String transcriptStatus = "Partial";
                if (!enrollments.isEmpty() && completedCourses == enrollments.size()) {
                    transcriptStatus = "Complete";
                } else if (enrollments.isEmpty()) {
                    transcriptStatus = "No Enrollments";
                }

                return Map.of(
                        "📚 Current Enrollments", String.valueOf(enrollments.size()),
                        "✅ Completed Courses", String.valueOf(completedCourses),
                        "📄 Transcript Status", transcriptStatus
                );
            }

            return Map.of("⚠️ Status", "No Role Assigned");

        } catch (Exception e) {
            System.err.println("Dashboard stats error: " + e.getMessage());
            e.printStackTrace();
            
            
            String errorMsg = "Connection Error";
            if (e.getMessage() != null) {
                if (e.getMessage().contains("database") || e.getMessage().contains("connection")) {
                    errorMsg = "Database Unavailable";
                } else if (e.getMessage().contains("session") || e.getMessage().contains("auth")) {
                    errorMsg = "Session Expired";
                }
            }
            
            return Map.of(
                "⚠️ Status", errorMsg,
                "🔄 Action", "Refresh or Restart",
                "📞 Support", "Contact IT"
            );
        }
    }

    private void switchToTab(int index) {
        if (tabs != null && index < tabs.getTabCount()) {
            tabs.setSelectedIndex(index);
        }
    }

    private void showSettings() {
        JOptionPane.showMessageDialog(this, "System Settings panel will be implemented.", "Settings", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showSchedule() {
        JOptionPane.showMessageDialog(this, "Schedule view will be implemented.", "Schedule", JOptionPane.INFORMATION_MESSAGE);
    }

    private String getCurrentSemester() {
        int month = java.time.LocalDate.now().getMonthValue();
        int year = java.time.LocalDate.now().getYear();
        if (month >= 1 && month <= 5) {
            return "Spring " + year;
        } else if (month >= 6 && month <= 8) {
            return "Summer " + year;
        } else {
            return "Fall " + year;
        }
    }

    
    private void openAddCourse() {
        showPanelDialog(new edu.univ.erp.ui.panels.AddCoursePanel(), "Add New Course");
    }

    private void openAddUser() {
        showPanelDialog(new edu.univ.erp.ui.panels.AddUserPanel(), "Add New User");
    }

    private void openAddSection() {
        showPanelDialog(new edu.univ.erp.ui.panels.AddSectionPanel(), "Add New Section");
    }

    private void openEnterGrades() {
        showPanelDialog(new edu.univ.erp.ui.panels.EnterScoresPanel(), "Enter Grades");
    }

    private void openAssessmentSetup() {
        showPanelDialog(new edu.univ.erp.ui.panels.AssessmentSetupPanel(), "Assessment Setup");
    }

    private void openComputeGrades() {
        showPanelDialog(new edu.univ.erp.ui.panels.ComputeGradesPanel(), "Compute Grades");
    }

    private void openCourseRegistration() {
        showPanelDialog(new edu.univ.erp.ui.panels.StudentRegisterPanel(), "Course Registration");
    }

    private void openMySchedule() {
        showPanelDialog(new edu.univ.erp.ui.panels.StudentTimetablePanel(), "My Schedule");
    }

    private void openStudentGrades() {
        showPanelDialog(new edu.univ.erp.ui.panels.StudentGradesPanel(), "My Grades");
    }

    private void openManageSections() {
        showPanelDialog(new edu.univ.erp.ui.panels.SectionManagementPanel(), "Manage Sections");
    }

    private void showPanelDialog(JPanel panel, String title) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.add(panel);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    
    private JPanel makeCoursesPanel() {
        return new edu.univ.erp.ui.panels.AddCoursePanel();
    }

    private JPanel makeUsersPanel() {
        return new edu.univ.erp.ui.panels.AddUserPanel();
    }



    private JPanel makeSectionsPanel() {
        return new edu.univ.erp.ui.panels.MySectionsPanel();
    }

    private JPanel makeGradesPanel() {
        return new edu.univ.erp.ui.panels.EnterScoresPanel();
    }

    private JPanel makeAnalyticsPanel() {
        return new edu.univ.erp.ui.panels.ComputeGradesPanel();
    }

    private JPanel makeMyCoursesPanel() {
        return new edu.univ.erp.ui.panels.StudentMyRegistrationsPanel();
    }

    private JPanel makeRegistrationPanel() {
        return new edu.univ.erp.ui.panels.StudentRegisterPanel();
    }

    private JPanel makeTranscriptPanel() {
        return new edu.univ.erp.ui.panels.StudentTranscriptPanel();
    }

    private JPanel createPlaceholderPanel(String title, String description) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ERPColors.BACKGROUND);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel content = ERPComponents.createCard();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(ERPFonts.TITLE);
        titleLabel.setForeground(ERPColors.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>", SwingConstants.CENTER);
        descLabel.setFont(ERPFonts.NORMAL);
        descLabel.setForeground(ERPColors.TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setBorder(new EmptyBorder(15, 0, 25, 0));

        JButton comingSoonBtn = new JButton("Coming Soon");
        comingSoonBtn.setFont(ERPFonts.NORMAL);
        comingSoonBtn.setBackground(ERPColors.PRIMARY);
        comingSoonBtn.setForeground(Color.WHITE);
        comingSoonBtn.setFocusPainted(false);
        comingSoonBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        comingSoonBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        comingSoonBtn.setEnabled(false);

        content.add(titleLabel);
        content.add(descLabel);
        content.add(comingSoonBtn);

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }
}
