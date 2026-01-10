package edu.univ.erp.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.util.List;

import edu.univ.erp.api.instructor.InstructorAPI;
import edu.univ.erp.domain.Section;
import edu.univ.erp.ui.theme.ERPFonts;

public class StatsPanel extends JPanel {

    private final InstructorAPI instructorAPI = new InstructorAPI();

    public StatsPanel() {
        setLayout(new GridLayout(1, 3, 20, 20));
        setBackground(new Color(245, 246, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        loadStats();
    }

    private void loadStats() {
        removeAll();
        setLayout(new GridLayout(1, 3, 20, 20));

        try {
            List<Section> sections = instructorAPI.getMySections();
            int sectionCount = sections.size();
            int students = sections.stream().mapToInt(Section::getEnrolledCount).sum();
            int seatsLeft = sections.stream().mapToInt(Section::getSeatsLeft).sum();

            add(makeCard("Sections Teaching", String.valueOf(sectionCount)));
            add(makeCard("Students Enrolled", String.valueOf(students)));
            add(makeCard("Seats Remaining", String.valueOf(seatsLeft)));
        } catch (Exception e) {
            add(makeCard("Stats Unavailable", e.getMessage()));
            add(makeCard("Sections", "-"));
            add(makeCard("Students", "-"));
        }

        revalidate();
        repaint();
    }

    private JPanel makeCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel t = new JLabel(title);
        t.setFont(ERPFonts.SUBTITLE);
        t.setForeground(new Color(40, 70, 150));

        JLabel v = new JLabel(value, SwingConstants.CENTER);
        v.setFont(new Font("Segoe UI", Font.BOLD, 26));
        v.setForeground(new Color(50, 130, 230));

        card.add(t, BorderLayout.NORTH);
        card.add(v, BorderLayout.CENTER);

        return card;
    }
}
