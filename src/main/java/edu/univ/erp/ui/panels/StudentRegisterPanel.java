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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import edu.univ.erp.api.catalog.CatalogAPI;
import edu.univ.erp.api.maintenance.MaintenanceAPI;
import edu.univ.erp.api.student.StudentAPI;
import edu.univ.erp.domain.Section;
import edu.univ.erp.ui.theme.ERPColors;
import edu.univ.erp.ui.theme.ERPFonts;

public class StudentRegisterPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private final StudentAPI studentAPI = new StudentAPI();
    private final MaintenanceAPI maintenanceAPI = new MaintenanceAPI();
    private final CatalogAPI catalogAPI = new CatalogAPI();
    private JComboBox<String> semesterCombo;
    private JComboBox<Integer> yearCombo;
    private List<Section> currentSections = new ArrayList<>();
    private static final long ADD_WINDOW_MILLIS = TimeUnit.DAYS.toMillis(7);

    public StudentRegisterPanel() {
        setLayout(new BorderLayout());
        setBackground(ERPColors.BACKGROUND);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(ERPColors.BACKGROUND);
        JLabel title = new JLabel("📘 Register for a Section", SwingConstants.LEFT);
        title.setFont(ERPFonts.TITLE);
        title.setForeground(ERPColors.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        titlePanel.add(title, BorderLayout.WEST);
        
        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setFont(ERPFonts.NORMAL);
        refreshBtn.setBackground(ERPColors.PRIMARY);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        JPanel filterPanel = new JPanel();
        filterPanel.setBackground(ERPColors.BACKGROUND);
        filterPanel.add(new JLabel("Semester:"));
        semesterCombo = new JComboBox<>();
        populateSemesters();
        filterPanel.add(semesterCombo);
        filterPanel.add(new JLabel("Year:"));
        yearCombo = new JComboBox<>();
        populateYears();
        filterPanel.add(yearCombo);
        JButton loadBtn = new JButton("Load Sections");
        loadBtn.addActionListener(e -> {
            String semester = (String) semesterCombo.getSelectedItem();
            Integer year = (Integer) yearCombo.getSelectedItem();
            if (semester == null || year == null) {
                JOptionPane.showMessageDialog(this, 
                    "Please select both semester and year before loading sections.", 
                    "Selection Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            loadSections(semester, year);
        });
        filterPanel.add(loadBtn);
        titlePanel.add(filterPanel, BorderLayout.SOUTH);
        
        refreshBtn.addActionListener(e -> {
            loadSections((String) semesterCombo.getSelectedItem(), (Integer) yearCombo.getSelectedItem());
        });
        titlePanel.add(refreshBtn, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);

        
        String[] columns = {
                "Section ID", "Course Code", "Course Title", "Instructor",
                "Day", "Time", "Room", "Capacity", "Seats Left", "Add Window", "Register"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 10 && !maintenanceAPI.isReadOnlyNow(); 
            }
        };

        table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(ERPFonts.NORMAL);
        
        
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        
        table.getColumnModel().getColumn(0).setPreferredWidth(80);  
        table.getColumnModel().getColumn(1).setPreferredWidth(100); 
        table.getColumnModel().getColumn(2).setPreferredWidth(200); 
        table.getColumnModel().getColumn(3).setPreferredWidth(120); 
        table.getColumnModel().getColumn(4).setPreferredWidth(80);  
        table.getColumnModel().getColumn(5).setPreferredWidth(100); 
        table.getColumnModel().getColumn(6).setPreferredWidth(80);  
        table.getColumnModel().getColumn(7).setPreferredWidth(70);  
        table.getColumnModel().getColumn(8).setPreferredWidth(80);  
        table.getColumnModel().getColumn(9).setPreferredWidth(220);  
        table.getColumnModel().getColumn(10).setPreferredWidth(100); 

        
        table.getColumnModel().getColumn(9).setCellRenderer(new MultilineCellRenderer());

        
        table.getColumn("Register").setCellRenderer(new ButtonRenderer());
        table.getColumn("Register").setCellEditor(new ButtonEditor(new JCheckBox()));

        add(new JScrollPane(table), BorderLayout.CENTER);

        
        SwingUtilities.invokeLater(() -> {
            if (semesterCombo.getItemCount() > 0 && yearCombo.getItemCount() > 0) {
                loadSections((String) semesterCombo.getSelectedItem(), (Integer) yearCombo.getSelectedItem());
            }
        });
    }

    private void loadSections(String semester, Integer year) {
        model.setRowCount(0);
        currentSections.clear();
        try {
            if (semester == null || year == null) {
                JOptionPane.showMessageDialog(this, "Please select both semester and year.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            java.util.List<Section> sections = studentAPI.getAvailableSections(semester, year);
            if (sections == null) {
                sections = new ArrayList<>();
            }
            currentSections = new ArrayList<>(sections);
            
            if (currentSections.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No available sections found for " + semester + " " + year + ".\n" +
                    "Please check if sections exist and have available seats.", 
                    "No Sections", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
            for (Section s : currentSections) {
                model.addRow(new Object[]{
                    s.getSectionId(),
                    s.getCourseCode() != null ? s.getCourseCode() : "N/A",
                    s.getCourseTitle() != null ? s.getCourseTitle() : "N/A",
                    s.getInstructorName() != null ? s.getInstructorName() : "TBA",
                    s.getDay() != null ? s.getDay() : "N/A",
                    s.getTime() != null ? s.getTime() : "N/A",
                    s.getRoom() != null ? s.getRoom() : "N/A",
                    s.getCapacity(),
                    s.getSeatsLeft(),
                    formatAddWindow(s),
                    "Register"
                });
            }
        } catch (Exception e) {
            currentSections = new ArrayList<>();
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading sections: " + e.getMessage() + "\n\n" +
                "Please check:\n" +
                "1. Database connection\n" +
                "2. Semester and year selection\n" +
                "3. Console for detailed error", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateSemesters() {
        semesterCombo.removeAllItems();
        try {
            java.util.List<String> semesters = catalogAPI.getAvailableSemesters();
            if (semesters.isEmpty()) {
                
                semesterCombo.addItem("Spring");
                semesterCombo.addItem("Fall");
                semesterCombo.addItem("Summer");
            } else {
                for (String semester : semesters) {
                    semesterCombo.addItem(semester);
                }
            }
        } catch (Exception e) {
            
            semesterCombo.addItem("Spring");
            semesterCombo.addItem("Fall");
            semesterCombo.addItem("Summer");
        }
    }

    private void populateYears() {
        yearCombo.removeAllItems();
        try {
            java.util.List<Integer> years = catalogAPI.getAvailableYears();
            if (years.isEmpty()) {
                
                int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
                for (int i = currentYear + 1; i >= currentYear - 2; i--) {
                    yearCombo.addItem(i);
                }
            } else {
                for (Integer year : years) {
                    yearCombo.addItem(year);
                }
            }
        } catch (Exception e) {
            
            int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
            for (int i = currentYear + 1; i >= currentYear - 2; i--) {
                yearCombo.addItem(i);
            }
        }
    }

    
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(50, 130, 230));
            setForeground(Color.WHITE);
        }
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            boolean canRegister = isAddWindowOpenForRow(row);
            setText(canRegister ? "Register" : "Closed");
            setEnabled(canRegister && !maintenanceAPI.isReadOnlyNow());
            setBackground(canRegister ? new Color(50, 130, 230) : Color.GRAY);
            return this;
        }
    }

    
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int sectionId;
        private int currentRow = -1;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Register");
            button.setBackground(new Color(50, 130, 230));
            button.setForeground(Color.WHITE);

            button.addActionListener(e -> {
                if (currentRow >= 0 && !isAddWindowOpenForRow(currentRow)) {
                    JOptionPane.showMessageDialog(StudentRegisterPanel.this,
                            "Add window has expired for this section.",
                            "Add Window Closed",
                            JOptionPane.WARNING_MESSAGE);
                    fireEditingStopped();
                    return;
                }
                if (sectionId <= 0) {
                    JOptionPane.showMessageDialog(StudentRegisterPanel.this, 
                        "Invalid section ID. Please try again.", "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    fireEditingStopped();
                    return;
                }
                button.setEnabled(false); 
                try {
                    String result = studentAPI.registerForSection(sectionId);
                    if (result != null && result.toLowerCase().contains("success")) {
                        JOptionPane.showMessageDialog(StudentRegisterPanel.this, result, "Success", JOptionPane.INFORMATION_MESSAGE);
                        
                        studentAPI.reloadStudentState();
                        
                        currentSections.clear();
                        
                        loadSections((String) semesterCombo.getSelectedItem(), (Integer) yearCombo.getSelectedItem());
                    } else {
                        JOptionPane.showMessageDialog(StudentRegisterPanel.this, result != null ? result : "Registration failed", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(StudentRegisterPanel.this, "Error: " + ex.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    button.setEnabled(true);
                    fireEditingStopped();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int col) {
            try {
                Object sectionIdObj = table.getValueAt(row, 0);
                if (sectionIdObj instanceof Integer) {
                    sectionId = (Integer) sectionIdObj;
                } else if (sectionIdObj instanceof Number) {
                    sectionId = ((Number) sectionIdObj).intValue();
                } else {
                    sectionId = Integer.parseInt(sectionIdObj.toString());
                }
                currentRow = row;
                boolean canRegister = isAddWindowOpenForRow(row);
                button.setEnabled(canRegister && !maintenanceAPI.isReadOnlyNow());
            } catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(StudentRegisterPanel.this, 
                    "Error getting section ID: " + e.getMessage(), "Error", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                sectionId = 0;
                currentRow = -1;
                button.setEnabled(false);
            }
            return button;
        }

        public Object getCellEditorValue() {
            return "Register";
        }
    }

    private boolean isAddWindowOpenForRow(int viewRow) {
        int modelRow = table.convertRowIndexToModel(viewRow);
        Section section = getSectionForModelRow(modelRow);
        return isAddWindowOpen(section);
    }

    private Section getSectionForModelRow(int modelRow) {
        if (modelRow >= 0 && modelRow < currentSections.size()) {
            return currentSections.get(modelRow);
        }
        return null;
    }

    private boolean isAddWindowOpen(Section section) {
        if (section == null) {
            return false;
        }
        if (section.getCreatedAt() == null) {
            
            return true;
        }
        try {
            long deadline = section.getCreatedAt().getTime() + ADD_WINDOW_MILLIS;
            return System.currentTimeMillis() <= deadline;
        } catch (Exception e) {
            
            return true;
        }
    }

    private String formatAddWindow(Section section) {
        if (section == null) {
            return "Add window remaining: N/A";
        }
        if (section.getCreatedAt() == null) {
            
            return "Add window remaining: Open";
        }
        try {
            long deadline = section.getCreatedAt().getTime() + ADD_WINDOW_MILLIS;
            long remaining = deadline - System.currentTimeMillis();
            if (remaining <= 0) {
                return "Add window remaining: 0 days 0 hours 0 minutes (Expired)";
            }
            long days = TimeUnit.MILLISECONDS.toDays(remaining);
            long hours = TimeUnit.MILLISECONDS.toHours(remaining - TimeUnit.DAYS.toMillis(days));
            long minutes = TimeUnit.MILLISECONDS.toMinutes(remaining - TimeUnit.DAYS.toMillis(days) - TimeUnit.HOURS.toMillis(hours));
            return String.format("Add window remaining: %d days %d hours %d minutes", days, hours, minutes);
        } catch (Exception e) {
            return "Add window remaining: Error calculating";
        }
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
