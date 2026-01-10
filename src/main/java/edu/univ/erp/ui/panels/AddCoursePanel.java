package edu.univ.erp.ui.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import edu.univ.erp.api.admin.AdminAPI;
import edu.univ.erp.domain.Course;
import edu.univ.erp.ui.theme.ERPColors;
import edu.univ.erp.ui.theme.ERPComponents;
import edu.univ.erp.ui.theme.ERPFonts;

public class AddCoursePanel extends JPanel {

    private final AdminAPI adminAPI = new AdminAPI();

    public AddCoursePanel() {
        setLayout(new BorderLayout());
        setBackground(ERPColors.BACKGROUND);

        JLabel title = new JLabel("Add New Course", SwingConstants.LEFT);
        title.setFont(ERPFonts.TITLE);
        title.setForeground(ERPColors.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        add(title, BorderLayout.NORTH);

        JPanel card = ERPComponents.createCard();
        card.setLayout(new GridLayout(5, 2, 12, 12));

        JTextField code = new JTextField();
        JTextField name = new JTextField();
        JTextField credits = new JTextField();
        JTextArea description = new JTextArea(3, 20);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);

        card.add(new JLabel("Course Code:"));
        card.add(code);

        card.add(new JLabel("Course Title:"));
        card.add(name);

        card.add(new JLabel("Credits:"));
        card.add(credits);

        card.add(new JLabel("Description:"));
        card.add(new javax.swing.JScrollPane(description));

        JButton save = ERPComponents.createPrimaryButton("Save Course");
        card.add(new JLabel(""));
        card.add(save);

        save.addActionListener(e -> {
            String codeText = code.getText().trim();
            String nameText = name.getText().trim();
            String creditsText = credits.getText().trim();
            String descText = description.getText().trim();

            if (codeText.isEmpty() || nameText.isEmpty() || creditsText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all required fields!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int creditsValue = Integer.parseInt(creditsText);
                if (creditsValue <= 0) {
                    JOptionPane.showMessageDialog(this, "Credits must be a positive number!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Course course = new Course();
                course.setCode(codeText);
                course.setTitle(nameText);
                course.setCredits(creditsValue);
                course.setDescription(descText.isEmpty() ? null : descText);

                String result = adminAPI.createCourse(course);
                JOptionPane.showMessageDialog(this, result, result.contains("successfully") ? "Success" : "Error", 
                    result.contains("successfully") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                
                if (result.contains("successfully")) {
                    // Clear fields
                    code.setText("");
                    name.setText("");
                    credits.setText("");
                    description.setText("");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Credits must be a valid number!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error creating course: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(ERPColors.BACKGROUND);
        wrapper.add(card, BorderLayout.NORTH);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(wrapper, BorderLayout.CENTER);
    }
}
