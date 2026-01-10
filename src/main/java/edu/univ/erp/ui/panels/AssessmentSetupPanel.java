package edu.univ.erp.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.univ.erp.api.instructor.InstructorAPI;
import edu.univ.erp.domain.AssessmentWeights;
import edu.univ.erp.domain.Section;
import edu.univ.erp.ui.theme.ERPColors;
import edu.univ.erp.ui.theme.ERPComponents;
import edu.univ.erp.ui.theme.ERPFonts;

public class AssessmentSetupPanel extends JPanel {

    private static final DateTimeFormatter UPDATED_AT_FORMAT =
            DateTimeFormatter.ofPattern("MMM d, yyyy hh:mm a");

    private final InstructorAPI instructorAPI = new InstructorAPI();
    private JComboBox<Section> sectionCombo;
    private JTextField quizField;
    private JTextField midtermField;
    private JTextField endSemField;
    private JLabel statusLabel;

    public AssessmentSetupPanel() {
        setLayout(new BorderLayout());
        setBackground(ERPColors.BACKGROUND);

        JLabel title = new JLabel("Assessment Setup", SwingConstants.LEFT);
        title.setFont(ERPFonts.TITLE);
        title.setForeground(ERPColors.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        add(title, BorderLayout.NORTH);

        JPanel card = ERPComponents.createCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        statusLabel = new JLabel("Select a section to load weights.");
        statusLabel.setFont(ERPFonts.SMALL);
        statusLabel.setForeground(ERPColors.TEXT_SECONDARY);
        card.add(statusLabel, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(4, 2, 12, 12));
        form.setOpaque(false);

        sectionCombo = new JComboBox<>();
        sectionCombo.setFont(ERPFonts.NORMAL);
        sectionCombo.addActionListener(e -> loadWeights());

        quizField = createWeightField();
        midtermField = createWeightField();
        endSemField = createWeightField();

        form.add(makeLabel("Section:"));
        form.add(sectionCombo);
        form.add(makeLabel("Quiz Weight (%):"));
        form.add(quizField);
        form.add(makeLabel("Midterm Weight (%):"));
        form.add(midtermField);
        form.add(makeLabel("End-Sem Weight (%):"));
        form.add(endSemField);

        card.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        javax.swing.JButton saveBtn = ERPComponents.createPrimaryButton("Save Weights");
        saveBtn.addActionListener(e -> saveWeights());
        actions.add(saveBtn);
        card.add(actions, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);

        SwingUtilities.invokeLater(this::loadSections);
    }

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.RIGHT);
        label.setFont(ERPFonts.NORMAL);
        label.setForeground(ERPColors.TEXT_PRIMARY);
        return label;
    }

    private JTextField createWeightField() {
        JTextField field = new JTextField();
        field.setFont(ERPFonts.NORMAL);
        field.setHorizontalAlignment(SwingConstants.RIGHT);
        field.setBackground(Color.WHITE);
        return field;
    }

    private void loadSections() {
        try {
            java.util.List<Section> sections = instructorAPI.getMySections();
            sectionCombo.removeAllItems();
            for (Section section : sections) {
                sectionCombo.addItem(section);
            }
            if (sections.isEmpty()) {
                statusLabel.setText("No sections assigned to you yet.");
                setWeights(20, 30, 50);
            } else {
                sectionCombo.setSelectedIndex(0);
                loadWeights();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading sections: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadWeights() {
        Section selected = (Section) sectionCombo.getSelectedItem();
        if (selected == null) {
            setWeights(20, 30, 50);
            return;
        }
        try {
            AssessmentWeights weights = instructorAPI.getAssessmentWeights(selected.getSectionId());
            if (weights != null) {
                setWeights(weights.getQuizWeight(), weights.getMidtermWeight(), weights.getEndSemWeight());
                if (weights.getUpdatedAt() != null) {
                    statusLabel.setText("Last updated: " + UPDATED_AT_FORMAT.format(weights.getUpdatedAt()));
                } else {
                    statusLabel.setText("Using default weights (20 / 30 / 50).");
                }
            } else {
                setWeights(20, 30, 50);
                statusLabel.setText("Using default weights (20 / 30 / 50).");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading assessment weights: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveWeights() {
        Section selected = (Section) sectionCombo.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a section first.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            double quiz = Double.parseDouble(quizField.getText().trim());
            double mid = Double.parseDouble(midtermField.getText().trim());
            double end = Double.parseDouble(endSemField.getText().trim());

            String result = instructorAPI.saveAssessmentWeights(selected.getSectionId(), quiz, mid, end);
            boolean success = result != null && result.toLowerCase().contains("success");
            JOptionPane.showMessageDialog(this,
                    result,
                    success ? "Success" : "Warning",
                    success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
            if (success) {
                loadWeights();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Weights must be numeric values.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving assessment weights: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setWeights(double quiz, double midterm, double endSem) {
        quizField.setText(String.format("%.2f", quiz));
        midtermField.setText(String.format("%.2f", midterm));
        endSemField.setText(String.format("%.2f", endSem));
    }
}
