package edu.univ.erp.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import edu.univ.erp.api.maintenance.MaintenanceAPI;
import edu.univ.erp.api.student.StudentAPI;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.ui.theme.ERPColors;
import edu.univ.erp.ui.theme.ERPFonts;

public class StudentMyRegistrationsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private final StudentAPI studentAPI = new StudentAPI();
    private final MaintenanceAPI maintenanceAPI = new MaintenanceAPI();
    private List<Enrollment> currentEnrollments = new ArrayList<>();

    public StudentMyRegistrationsPanel() {
        setLayout(new BorderLayout());
        setBackground(ERPColors.BACKGROUND);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(ERPColors.BACKGROUND);
        
        JLabel title = new JLabel("📚 My Registrations", SwingConstants.LEFT);
        title.setFont(ERPFonts.TITLE);
        title.setForeground(ERPColors.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        titlePanel.add(title, BorderLayout.WEST);
        
        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setFont(ERPFonts.NORMAL);
        refreshBtn.setBackground(ERPColors.PRIMARY);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        refreshBtn.addActionListener(e -> loadEnrollments());
        titlePanel.add(refreshBtn, BorderLayout.EAST);
        
        add(titlePanel, BorderLayout.NORTH);

        String[] columns = {
                "Enrollment ID", "Course Code", "Course Title", "Instructor",
                "Schedule", "Seats Left", "Drop Remaining", "Drop"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 7 && !maintenanceAPI.isReadOnlyNow(); 
            }
        };

        table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(ERPFonts.NORMAL);
        
        
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        
        table.getColumnModel().getColumn(0).setPreferredWidth(100); 
        table.getColumnModel().getColumn(1).setPreferredWidth(100); 
        table.getColumnModel().getColumn(2).setPreferredWidth(200); 
        table.getColumnModel().getColumn(3).setPreferredWidth(120); 
        table.getColumnModel().getColumn(4).setPreferredWidth(150); 
        table.getColumnModel().getColumn(5).setPreferredWidth(80);  
        table.getColumnModel().getColumn(6).setPreferredWidth(220); 
        table.getColumnModel().getColumn(7).setPreferredWidth(100); 

        
        table.getColumnModel().getColumn(6).setCellRenderer(new MultilineCellRenderer());

        table.getColumn("Drop").setCellRenderer(new DropRenderer());
        table.getColumn("Drop").setCellEditor(new DropEditor(new JCheckBox()));

        add(new JScrollPane(table), BorderLayout.CENTER);

        
        SwingUtilities.invokeLater(this::loadEnrollments);
    }

    private void loadEnrollments() {
        model.setRowCount(0);
        currentEnrollments.clear(); 
        try {
            
            java.util.List<Enrollment> enrollments = studentAPI.getMyEnrollments();
            currentEnrollments = enrollments != null ? new ArrayList<>(enrollments) : new ArrayList<>();
            for (Enrollment e : currentEnrollments) {
                model.addRow(new Object[]{
                    e.getEnrollmentId(),
                    e.getCourseCode(),
                    e.getCourseTitle(),
                    e.getInstructorName() != null ? e.getInstructorName() : "TBA",
                    buildSchedule(e),
                    seatsLeft(e),
                    formatDropWindow(e),
                    "Drop"
                });
            }
        } catch (Exception e) {
            currentEnrollments = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "Error loading enrollments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String buildSchedule(Enrollment enrollment) {
        if (enrollment.getSectionDay() != null && enrollment.getSectionTime() != null) {
            String room = enrollment.getSectionRoom() != null ? " @ " + enrollment.getSectionRoom() : "";
            return enrollment.getSectionDay() + " " + enrollment.getSectionTime() + room;
        }
        return enrollment.getSectionInfo() != null ? enrollment.getSectionInfo() : "N/A";
    }

    private String seatsLeft(Enrollment enrollment) {
        if (enrollment.getCapacity() != null && enrollment.getEnrolledCount() != null) {
            int left = Math.max(enrollment.getCapacity() - enrollment.getEnrolledCount(), 0);
            return left + " / " + enrollment.getCapacity();
        }
        return "N/A";
    }

    class DropRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public DropRenderer() {
            setOpaque(true);
            setBackground(new Color(200, 60, 60));
            setForeground(Color.WHITE);
        }

        public Component getTableCellRendererComponent(JTable t, Object v,
                                                       boolean sel, boolean fcs,
                                                       int row, int col) {
            boolean canDrop = isDropWindowOpenForRow(row);
            setText(canDrop ? "Drop" : "Closed");
            setEnabled(canDrop && !maintenanceAPI.isReadOnlyNow());
            setBackground(canDrop ? new Color(200, 60, 60) : Color.GRAY);
            return this;
        }
    }

    class DropEditor extends DefaultCellEditor {

        private JButton btn;
        private int enrollmentId;
        private int currentRow = -1;

        public DropEditor(JCheckBox checkBox) {
            super(checkBox);
            btn = new JButton("Drop");
            btn.setBackground(new Color(200, 60, 60));
            btn.setForeground(Color.WHITE);

            btn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(
                    StudentMyRegistrationsPanel.this,
                    "Are you sure you want to drop this section?",
                    "Confirm Drop",
                    JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    if (currentRow >= 0 && !isDropWindowOpenForRow(currentRow)) {
                        JOptionPane.showMessageDialog(StudentMyRegistrationsPanel.this,
                                "Drop window has expired for this enrollment.",
                                "Drop Window Closed",
                                JOptionPane.WARNING_MESSAGE);
                        fireEditingStopped();
                        return;
                    }
                    if (enrollmentId <= 0) {
                        JOptionPane.showMessageDialog(StudentMyRegistrationsPanel.this, 
                            "Invalid enrollment ID. Please try again.", "Error", 
                            JOptionPane.ERROR_MESSAGE);
                        fireEditingStopped();
                        return;
                    }
                    btn.setEnabled(false); 
                    try {
                        String result = studentAPI.dropSection(enrollmentId);
                        if (result != null && result.toLowerCase().contains("success")) {
                            JOptionPane.showMessageDialog(StudentMyRegistrationsPanel.this, result, "Success", JOptionPane.INFORMATION_MESSAGE);
                            
                            studentAPI.reloadStudentState();
                            
                            currentEnrollments.clear();
                            
                            loadEnrollments();
                        } else {
                            JOptionPane.showMessageDialog(StudentMyRegistrationsPanel.this, result != null ? result : "Drop failed", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(StudentMyRegistrationsPanel.this, "Error: " + ex.getMessage(), "Drop Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        btn.setEnabled(true);
                    }
                }
                fireEditingStopped();
            });
        }

        public Component getTableCellEditorComponent(JTable t, Object v,
                                                     boolean isSelected, int row, int col) {
            try {
                Object enrollmentIdObj = t.getValueAt(row, 0);
                if (enrollmentIdObj instanceof Integer) {
                    enrollmentId = (Integer) enrollmentIdObj;
                } else if (enrollmentIdObj instanceof Number) {
                    enrollmentId = ((Number) enrollmentIdObj).intValue();
                } else {
                    enrollmentId = Integer.parseInt(enrollmentIdObj.toString());
                }
                currentRow = row;
                boolean canDrop = isDropWindowOpenForRow(row);
                btn.setEnabled(canDrop && !maintenanceAPI.isReadOnlyNow());
            } catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(StudentMyRegistrationsPanel.this, 
                    "Error getting enrollment ID: " + e.getMessage(), "Error", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                enrollmentId = 0;
                currentRow = -1;
                btn.setEnabled(false);
            }
            return btn;
        }

        public Object getCellEditorValue() {
            return "Drop";
        }
    }

    private boolean isDropWindowOpenForRow(int viewRow) {
        int modelRow = table.convertRowIndexToModel(viewRow);
        Enrollment enrollment = getEnrollmentForModelRow(modelRow);
        return isDropWindowOpen(enrollment);
    }

    private Enrollment getEnrollmentForModelRow(int modelRow) {
        if (modelRow >= 0 && modelRow < currentEnrollments.size()) {
            return currentEnrollments.get(modelRow);
        }
        return null;
    }

    private boolean isDropWindowOpen(Enrollment enrollment) {
        if (enrollment == null || enrollment.getDropDeadline() == null) {
            return true;
        }
        long remaining = enrollment.getDropDeadline().getTime() - System.currentTimeMillis();
        return remaining >= 0;
    }

    private String formatDropWindow(Enrollment enrollment) {
        if (enrollment == null || enrollment.getDropDeadline() == null) {
            return "Drop remaining: N/A";
        }
        long remaining = enrollment.getDropDeadline().getTime() - System.currentTimeMillis();
        if (remaining <= 0) {
            return "Drop remaining: 0 days 0 hours 0 minutes (Expired)";
        }
        long days = TimeUnit.MILLISECONDS.toDays(remaining);
        long hours = TimeUnit.MILLISECONDS.toHours(remaining - TimeUnit.DAYS.toMillis(days));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(remaining - TimeUnit.DAYS.toMillis(days) - TimeUnit.HOURS.toMillis(hours));
        return String.format("Drop remaining: %d days %d hours %d minutes", days, hours, minutes);
    }

    
    static class MultilineCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        private javax.swing.JTextArea textArea;

        public MultilineCellRenderer() {
            textArea = new javax.swing.JTextArea();
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setOpaque(true);
            textArea.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            String text = value != null ? value.toString() : "";
            textArea.setText(text);
            textArea.setFont(table.getFont());
            
            
            int width = table.getColumnModel().getColumn(column).getWidth();
            textArea.setSize(width, Short.MAX_VALUE);
            int height = textArea.getPreferredSize().height;
            if (table.getRowHeight(row) < height) {
                table.setRowHeight(row, height);
            }
            
            if (isSelected) {
                textArea.setBackground(table.getSelectionBackground());
                textArea.setForeground(table.getSelectionForeground());
            } else {
                textArea.setBackground(table.getBackground());
                textArea.setForeground(table.getForeground());
            }
            
            return textArea;
        }
    }
}
