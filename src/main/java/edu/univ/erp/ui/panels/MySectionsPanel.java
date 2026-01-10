package edu.univ.erp.ui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import edu.univ.erp.api.instructor.InstructorAPI;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.ui.theme.ERPColors;
import edu.univ.erp.ui.theme.ERPComponents;
import edu.univ.erp.ui.theme.ERPFonts;

public class MySectionsPanel extends JPanel {

    private final InstructorAPI instructorAPI = new InstructorAPI();
    private DefaultTableModel tableModel;
    private JTable sectionsTable;
    private JLabel courseLabel;
    private JLabel scheduleLabel;
    private JLabel capacityLabel;
    private DefaultListModel<String> rosterModel;
    private java.util.List<Section> sections = java.util.Collections.emptyList();

    public MySectionsPanel() {
        setLayout(new BorderLayout());
        setBackground(ERPColors.BACKGROUND);

        JLabel title = new JLabel("📚 My Sections", SwingConstants.LEFT);
        title.setFont(ERPFonts.TITLE);
        title.setForeground(ERPColors.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        add(title, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"Section ID", "Course Code", "Course Title", "Semester", "Year", "Enrolled"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        sectionsTable = new JTable(tableModel);
        sectionsTable.setRowHeight(28);
        sectionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sectionsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = sectionsTable.getSelectedRow();
                if (row >= 0 && row < sections.size()) {
                    showDetails(sections.get(row));
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(sectionsTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        JPanel detailCard = ERPComponents.createCard();
        detailCard.setPreferredSize(new Dimension(320, 0));
        detailCard.setLayout(new BorderLayout(10, 10));

        JPanel summary = new JPanel(new GridLayout(0, 1, 4, 4));
        summary.setOpaque(false);
        courseLabel = new JLabel("Select a section", SwingConstants.LEFT);
        courseLabel.setFont(ERPFonts.SUBTITLE);
        scheduleLabel = new JLabel("Schedule: -", SwingConstants.LEFT);
        capacityLabel = new JLabel("Capacity: -", SwingConstants.LEFT);
        summary.add(courseLabel);
        summary.add(scheduleLabel);
        summary.add(capacityLabel);

        detailCard.add(summary, BorderLayout.NORTH);

        rosterModel = new DefaultListModel<>();
        JList<String> rosterList = new JList<>(rosterModel);
        rosterList.setBorder(BorderFactory.createTitledBorder("Enrolled Students"));
        JScrollPane rosterScroll = new JScrollPane(rosterList);
        detailCard.add(rosterScroll, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadSections());
        JPanel footer = new JPanel();
        footer.setBackground(ERPColors.BACKGROUND);
        footer.add(refreshBtn);

        detailCard.add(footer, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScroll, detailCard);
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        add(splitPane, BorderLayout.CENTER);

        SwingUtilities.invokeLater(this::loadSections);
    }

    private void loadSections() {
        tableModel.setRowCount(0);
        rosterModel.clear();
        try {
            sections = instructorAPI.getMySections();
            for (Section s : sections) {
                tableModel.addRow(new Object[]{
                        s.getSectionId(),
                        s.getCourseCode(),
                        s.getCourseTitle(),
                        s.getSemester(),
                        s.getYear(),
                        s.getEnrolledCount()
                });
            }
            if (!sections.isEmpty()) {
                sectionsTable.setRowSelectionInterval(0, 0);
                showDetails(sections.get(0));
            } else {
                showDetails(null);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading sections: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showDetails(Section section) {
        rosterModel.clear();
        if (section == null) {
            courseLabel.setText("No sections assigned");
            scheduleLabel.setText("Schedule: -");
            capacityLabel.setText("Capacity: -");
            return;
        }

        courseLabel.setText(section.getCourseCode() + " • " + section.getCourseTitle());
        scheduleLabel.setText(String.format("Schedule: %s | %s @ %s",
                section.getDay(),
                section.getTime(),
                section.getRoom()));
        capacityLabel.setText(String.format("Capacity: %d/%d (Seats left: %d)",
                section.getEnrolledCount(),
                section.getCapacity(),
                section.getSeatsLeft()));

        loadRoster(section.getSectionId());
    }

    private void loadRoster(int sectionId) {
        rosterModel.clear();
        try {
            List<Enrollment> enrollments = instructorAPI.getSectionEnrollments(sectionId);
            if (enrollments.isEmpty()) {
                rosterModel.addElement("No students enrolled yet.");
            } else {
                for (Enrollment enrollment : enrollments) {
                    String name = enrollment.getStudentName() != null
                            ? enrollment.getStudentName()
                            : "Student #" + enrollment.getEnrollmentId();
                    rosterModel.addElement(name);
                }
            }
        } catch (Exception e) {
            rosterModel.addElement("Unable to load roster: " + e.getMessage());
        }
    }
}
