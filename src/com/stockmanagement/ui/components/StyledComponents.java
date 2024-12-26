package com.stockmanagement.ui.components;

import com.stockmanagement.config.AppConfig;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StyledComponents {

    public static JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setPreferredSize(new Dimension(200, 30));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppConfig.THEME_COLOR, 1),
                new EmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    public static JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setPreferredSize(new Dimension(200, 30));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppConfig.THEME_COLOR, 1),
                new EmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(AppConfig.BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(200, 35));
        button.setFont(new Font("Arial", Font.BOLD, 14));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(AppConfig.HOVER_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(AppConfig.BUTTON_COLOR);
            }
        });

        return button;
    }
}