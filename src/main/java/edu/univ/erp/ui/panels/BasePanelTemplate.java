package edu.univ.erp.ui.panels;

import javax.swing.*;
import java.awt.*;
import edu.univ.erp.ui.theme.*;

public class BasePanelTemplate extends JPanel {

    public BasePanelTemplate(String title) {
        setLayout(new BorderLayout());
        setBackground(ERPColors.BACKGROUND);

        JLabel heading = new JLabel(title);
        heading.setFont(ERPFonts.TITLE);
        heading.setForeground(ERPColors.TEXT_PRIMARY);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        add(heading, BorderLayout.NORTH);
    }
}
