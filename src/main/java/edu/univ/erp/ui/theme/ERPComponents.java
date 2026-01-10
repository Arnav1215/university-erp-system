package edu.univ.erp.ui.theme;

import java.awt.Color;
import java.awt.Cursor;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ERPComponents {

    public static JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(ERPColors.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ERPColors.CARD_BORDER),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        return card;
    }

    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(ERPFonts.NORMAL);
        btn.setBackground(new Color(50, 130, 230));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return btn;
    }
}
