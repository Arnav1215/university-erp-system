package edu.univ.erp.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import edu.univ.erp.api.student.StudentAPI;
import edu.univ.erp.domain.Enrollment;

import edu.univ.erp.ui.theme.ERPColors;
import edu.univ.erp.ui.theme.ERPFonts;

public class StudentTimetablePanel extends JPanel {

    private final StudentAPI studentAPI = new StudentAPI();
    private DefaultTableModel model;

    public StudentTimetablePanel() {

        setLayout(new BorderLayout());
        setBackground(ERPColors.BACKGROUND);

        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(ERPColors.BACKGROUND);
        
        JLabel title = new JLabel("🗓 Weekly Timetable", SwingConstants.LEFT);
        title.setFont(ERPFonts.TITLE);
        title.setForeground(ERPColors.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        titlePanel.add(title, BorderLayout.WEST);
        
        model = new DefaultTableModel(
                new Object[]{"Course Code", "Course Title", "Day", "Time", "Room", "Instructor"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setFont(ERPFonts.NORMAL);
        refreshBtn.setBackground(ERPColors.PRIMARY);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        refreshBtn.addActionListener(e -> loadTimetable());
        titlePanel.add(refreshBtn, BorderLayout.EAST);
        
        add(titlePanel, BorderLayout.NORTH);

        JTable table = new JTable(model);
        styleTable(table);

        loadTimetable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadTimetable() {
        model.setRowCount(0);
        try {
            java.util.List<Enrollment> enrollments = studentAPI.getMyEnrollments();
            if (enrollments.isEmpty()) {
                model.addRow(new Object[]{"-", "No registrations yet", "-", "-", "-", "-"});
                return;
            }
            for (Enrollment enrollment : enrollments) {
                String day = enrollment.getSectionDay() != null ? enrollment.getSectionDay() : "-";
                String time = enrollment.getSectionTime() != null ? enrollment.getSectionTime() : "-";
                String room = enrollment.getSectionRoom() != null ? enrollment.getSectionRoom() : "-";
                String instructor = enrollment.getInstructorName() != null ? enrollment.getInstructorName() : "TBA";

                model.addRow(new Object[]{
                    enrollment.getCourseCode() != null ? enrollment.getCourseCode() : "-",
                    enrollment.getCourseTitle() != null ? enrollment.getCourseTitle() : "-",
                    day,
                    time,
                    room,
                    instructor
                });
            }
        } catch (Exception e) {
            model.addRow(new Object[]{"-", "Unable to load timetable: " + e.getMessage(), "-", "-", "-", "-"});
        }
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(ERPFonts.NORMAL);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(ERPFonts.SUBTITLE);
        header.setBackground(new Color(230, 235, 245));
        header.setForeground(ERPColors.TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(0, 40));
    }
}
