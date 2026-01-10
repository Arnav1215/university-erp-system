package edu.univ.erp.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import edu.univ.erp.api.student.StudentAPI;
import edu.univ.erp.domain.Student;
import edu.univ.erp.ui.theme.ERPColors;
import edu.univ.erp.ui.theme.ERPComponents;
import edu.univ.erp.ui.theme.ERPFonts;

public class StudentProfilePanel extends JPanel {

    private final StudentAPI studentAPI = new StudentAPI();
    private JLabel nameLabel, rollLabel, emailLabel, programLabel;

    public StudentProfilePanel() {

        setLayout(new BorderLayout());
        setBackground(ERPColors.BACKGROUND);

        
        JLabel title = new JLabel("My Profile", SwingConstants.LEFT);
        title.setFont(ERPFonts.TITLE);
        title.setForeground(ERPColors.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        add(title, BorderLayout.NORTH);

        
        JPanel card = ERPComponents.createCard();
        card.setPreferredSize(new Dimension(600, 350));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        
        JLabel photo = new JLabel("📷", SwingConstants.CENTER);
        photo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        photo.setPreferredSize(new Dimension(120, 120));
        photo.setOpaque(true);
        photo.setBackground(new Color(230, 230, 230));
        photo.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        photo.setHorizontalAlignment(SwingConstants.CENTER);

        
        nameLabel = infoLabel("Name:", "Loading...");
        rollLabel = infoLabel("Roll No:", "Loading...");
        emailLabel = infoLabel("Email:", "Loading...");
        programLabel = infoLabel("Program:", "N/A");

        gbc.gridy = 0;
        content.add(photo, gbc);

        gbc.gridy++;
        content.add(Box.createVerticalStrut(15), gbc);

        gbc.gridy++;
        content.add(nameLabel, gbc);

        gbc.gridy++;
        content.add(rollLabel, gbc);

        gbc.gridy++;
        content.add(emailLabel, gbc);

        gbc.gridy++;
        content.add(programLabel, gbc);

        card.add(content, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(card);

        add(wrapper, BorderLayout.CENTER);

        
        javax.swing.SwingUtilities.invokeLater(this::loadProfile);
    }

    private void loadProfile() {
        try {
            Student student = studentAPI.getProfile();
            if (student != null) {
                nameLabel.setText("Name:  " + (student.getFullName() != null ? student.getFullName() : "N/A"));
                rollLabel.setText("Roll No:  " + (student.getRollNo() != null ? student.getRollNo() : "N/A"));
                emailLabel.setText("Email:  " + (student.getEmail() != null ? student.getEmail() : "N/A"));
                programLabel.setText("Program:  " + (student.getProgram() != null ? student.getProgram() : "N/A"));
            } else {
                nameLabel.setText("Name:  Error loading profile");
                rollLabel.setText("Roll No:  N/A");
                emailLabel.setText("Email:  N/A");
                programLabel.setText("Program:  N/A");
            }
        } catch (Exception e) {
            nameLabel.setText("Name:  Error loading profile");
            rollLabel.setText("Roll No:  N/A");
            emailLabel.setText("Email:  N/A");
            programLabel.setText("Program:  N/A");
        }
    }

    private JLabel infoLabel(String title, String value) {
        JLabel lbl = new JLabel(title + "  " + value);
        lbl.setFont(ERPFonts.NORMAL);
        lbl.setForeground(ERPColors.TEXT_PRIMARY);
        lbl.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        return lbl;
    }
}
