package edu.univ.erp.ui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
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

import edu.univ.erp.api.admin.AdminAPI;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Section;
import edu.univ.erp.ui.theme.ERPColors;
import edu.univ.erp.ui.theme.ERPComponents;
import edu.univ.erp.ui.theme.ERPFonts;

public class SectionManagementPanel extends JPanel {

    private final AdminAPI adminAPI = new AdminAPI();
    private final DefaultTableModel sectionTableModel;
    private final JTable sectionsTable;
    private final JComboBox<InstructorOption> instructorCombo;
    private final DefaultListModel<String> rosterModel;
    private final JLabel scheduleLabel;
    private final JLabel capacityLabel;
    private final JLabel courseLabel;
    private final JButton assignButton;

    private List<Section> sections = new ArrayList<>();
    private List<Instructor> instructors = new ArrayList<>();
    private Section selectedSection;

    public SectionManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(ERPColors.BACKGROUND);

        JLabel title = new JLabel("👥 Manage Sections & Assign Instructors", SwingConstants.LEFT);
        title.setFont(ERPFonts.TITLE);
        title.setForeground(ERPColors.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        add(title, BorderLayout.NORTH);

        String[] columns = {"Section ID", "Course", "Instructor", "Day", "Time", "Room", "Seats"};
        sectionTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        sectionsTable = new JTable(sectionTableModel);
        sectionsTable.setRowHeight(30);
        sectionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sectionsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = sectionsTable.getSelectedRow();
                if (row >= 0 && row < sections.size()) {
                    showSectionDetails(sections.get(row));
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(sectionsTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        JPanel detailCard = ERPComponents.createCard();
        detailCard.setPreferredSize(new Dimension(350, 0));
        detailCard.setLayout(new BorderLayout(10, 10));

        JPanel detailHeader = new JPanel(new GridLayout(0, 1, 4, 4));
        detailHeader.setOpaque(false);
        courseLabel = new JLabel("Select a section", SwingConstants.LEFT);
        courseLabel.setFont(ERPFonts.SUBTITLE);
        scheduleLabel = new JLabel("Schedule: -", SwingConstants.LEFT);
        capacityLabel = new JLabel("Capacity: -", SwingConstants.LEFT);
        detailHeader.add(courseLabel);
        detailHeader.add(scheduleLabel);
        detailHeader.add(capacityLabel);

        detailCard.add(detailHeader, BorderLayout.NORTH);

        JPanel assignmentPanel = new JPanel(new GridLayout(0, 1, 6, 6));
        assignmentPanel.setOpaque(false);
        assignmentPanel.setBorder(BorderFactory.createTitledBorder("Assign Instructor"));

        instructorCombo = new JComboBox<>();
        assignmentPanel.add(instructorCombo);

        assignButton = new JButton("Assign Instructor");
        assignButton.addActionListener(e -> assignInstructor());
        assignmentPanel.add(assignButton);

        detailCard.add(assignmentPanel, BorderLayout.CENTER);

        rosterModel = new DefaultListModel<>();
        JList<String> rosterList = new JList<>(rosterModel);
        JScrollPane rosterScroll = new JScrollPane(rosterList);
        rosterScroll.setBorder(BorderFactory.createTitledBorder("Enrolled Students"));
        rosterScroll.setPreferredSize(new Dimension(300, 180));

        detailCard.add(rosterScroll, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScroll, detailCard);
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        add(splitPane, BorderLayout.CENTER);

        JPanel footer = new JPanel();
        footer.setBackground(ERPColors.BACKGROUND);
        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.addActionListener(e -> loadData());
        footer.add(refreshButton);

        add(footer, BorderLayout.SOUTH);

        SwingUtilities.invokeLater(this::loadData);
    }

    private void loadData() {
        loadInstructors();
        loadSections();
    }

    private void loadInstructors() {
        instructorCombo.removeAllItems();
        instructorCombo.addItem(new InstructorOption("Unassigned", null));
        try {
            instructors = adminAPI.getAllInstructors();
            for (Instructor instructor : instructors) {
                instructorCombo.addItem(new InstructorOption(instructor));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to load instructors: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSections() {
        sectionTableModel.setRowCount(0);
        sections = new ArrayList<>();
        try {
            sections = adminAPI.getAllSections();
            for (Section section : sections) {
                sectionTableModel.addRow(new Object[]{
                    section.getSectionId(),
                    section.getCourseCode() + " - " + section.getCourseTitle(),
                    section.getInstructorName() != null ? section.getInstructorName() : "Unassigned",
                    section.getDay(),
                    section.getTime(),
                    section.getRoom(),
                    section.getEnrolledCount() + "/" + section.getCapacity()
                });
            }

            if (!sections.isEmpty()) {
                sectionsTable.setRowSelectionInterval(0, 0);
                showSectionDetails(sections.get(0));
            } else {
                showSectionDetails(null);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to load sections: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSectionDetails(Section section) {
        selectedSection = section;
        rosterModel.clear();

        if (section == null) {
            courseLabel.setText("No sections available");
            scheduleLabel.setText("Schedule: -");
            capacityLabel.setText("Capacity: -");
            assignButton.setEnabled(false);
            return;
        }

        courseLabel.setText(section.getCourseCode() + " • " + section.getCourseTitle());
        scheduleLabel.setText(String.format("Schedule: %s | %s @ %s",
                section.getDay(), section.getTime(), section.getRoom()));
        capacityLabel.setText(String.format("Capacity: %d/%d (Seats left: %d)",
                section.getEnrolledCount(), section.getCapacity(), section.getSeatsLeft()));
        assignButton.setEnabled(true);

        selectInstructorInCombo(section.getInstructorId());
        loadRoster(section.getSectionId());
    }

    private void selectInstructorInCombo(Integer instructorId) {
        for (int i = 0; i < instructorCombo.getItemCount(); i++) {
            InstructorOption option = instructorCombo.getItemAt(i);
            if ((option.userId == null && instructorId == null) ||
                (instructorId != null && instructorId.equals(option.userId))) {
                instructorCombo.setSelectedIndex(i);
                return;
            }
        }
        instructorCombo.setSelectedIndex(0);
    }

    private void loadRoster(int sectionId) {
        rosterModel.clear();
        try {
            List<Enrollment> enrollments = adminAPI.getSectionEnrollments(sectionId);
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

    private void assignInstructor() {
        if (selectedSection == null) {
            JOptionPane.showMessageDialog(this, "Select a section first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        InstructorOption option = (InstructorOption) instructorCombo.getSelectedItem();
        Integer instructorId = option != null ? option.userId : null;

        String result = adminAPI.assignInstructor(selectedSection.getSectionId(), instructorId);
        JOptionPane.showMessageDialog(this, result,
                result.contains("successfully") ? "Success" : "Status",
                result.contains("successfully") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);

        loadSections();
    }

    private record InstructorOption(String label, Integer userId) {
        InstructorOption(String label, Integer userId) {
            this.label = label;
            this.userId = userId;
        }

        InstructorOption(Instructor instructor) {
            this(instructor.getFullName() != null ? instructor.getFullName() : instructor.getEmail(),
                    instructor.getUserId());
        }

        @Override
        public String toString() {
            return label;
        }
    }
}

