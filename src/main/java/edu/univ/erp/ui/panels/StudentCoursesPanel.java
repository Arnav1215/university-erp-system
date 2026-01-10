package edu.univ.erp.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

import edu.univ.erp.api.student.StudentAPI;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.ui.theme.ERPColors;
import edu.univ.erp.ui.theme.ERPFonts;

public class StudentCoursesPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private final StudentAPI studentAPI = new StudentAPI();

    public StudentCoursesPanel() {

        setLayout(new BorderLayout());
        setBackground(ERPColors.BACKGROUND);

        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(ERPColors.BACKGROUND);
        
        JLabel title = new JLabel("📘 My Courses", SwingConstants.LEFT);
        title.setFont(ERPFonts.TITLE);
        title.setForeground(ERPColors.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));
        titlePanel.add(title, BorderLayout.WEST);
        
        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setFont(ERPFonts.NORMAL);
        refreshBtn.setBackground(ERPColors.PRIMARY);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        refreshBtn.addActionListener(e -> loadCourses());
        titlePanel.add(refreshBtn, BorderLayout.EAST);
        
        add(titlePanel, BorderLayout.NORTH);

        
        JPanel filterBar = new JPanel();
        filterBar.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterBar.setBackground(ERPColors.BACKGROUND);

        
        JTextField search = new JTextField(18);
        search.setFont(ERPFonts.NORMAL);
        search.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180,180,180)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        filterBar.add(new JLabel("Search: "));
        filterBar.add(search);

        add(filterBar, BorderLayout.CENTER);

        
        String[] cols = {"Course Code", "Course Title", "Instructor", "Schedule", "Status"};

        model = new DefaultTableModel(cols, 0);

        loadCourses();

        table = new JTable(model);
        styleTable(table);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        add(sp, BorderLayout.SOUTH);

        
        search.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filter(search.getText());
            }
        });
    }

    private void loadCourses() {
        model.setRowCount(0);
        try {
            
            java.util.List<Enrollment> enrollments = studentAPI.getMyEnrollments();
            if (enrollments.isEmpty()) {
                model.addRow(new Object[]{"-", "No courses enrolled yet", "-", "-", "-"});
                return;
            }
            
            
            Set<String> seenCourses = new HashSet<>();
            for (Enrollment enrollment : enrollments) {
                String courseCode = enrollment.getCourseCode();
                if (courseCode != null && !seenCourses.contains(courseCode)) {
                    seenCourses.add(courseCode);
                    String schedule = buildSchedule(enrollment);
                    String instructor = enrollment.getInstructorName() != null ? enrollment.getInstructorName() : "TBA";
                    
                    model.addRow(new Object[]{
                        courseCode,
                        enrollment.getCourseTitle() != null ? enrollment.getCourseTitle() : "-",
                        instructor,
                        schedule,
                        "Enrolled"
                    });
                }
            }
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error loading courses: " + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String buildSchedule(Enrollment enrollment) {
        if (enrollment.getSectionDay() != null && enrollment.getSectionTime() != null) {
            String room = enrollment.getSectionRoom() != null ? " @ " + enrollment.getSectionRoom() : "";
            return enrollment.getSectionDay() + " " + enrollment.getSectionTime() + room;
        }
        return enrollment.getSectionInfo() != null ? enrollment.getSectionInfo() : "N/A";
    }

    
    private void filter(String text) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        if (!text.isEmpty()) {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        } else {
            sorter.setRowFilter(null);
        }
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(ERPFonts.NORMAL);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(ERPFonts.SUBTITLE);
        header.setBackground(new Color(230,235,245));
        header.setForeground(ERPColors.TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(0, 40));
    }
}
