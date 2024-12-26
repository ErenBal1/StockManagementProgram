package com.stockmanagement.ui.panels;

import com.stockmanagement.config.AppConfig;
import com.stockmanagement.ui.components.GradientPanel;
import com.stockmanagement.ui.components.StyledComponents;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginPanel extends GradientPanel {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final LoginCallback callback;

    public interface LoginCallback {
        void onLoginSuccess();
        void onLoginFailed();
    }

    public LoginPanel(LoginCallback callback) {
        this.callback = callback;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel(AppConfig.APP_TITLE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Form components
        usernameField = StyledComponents.createStyledTextField();
        passwordField = new JPasswordField(20);
        JButton loginButton = StyledComponents.createStyledButton("Login");

        // Add key listeners for Enter key
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    checkLogin();
                }
            }
        };

        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);

        // Layout
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridy = 1; gbc.gridwidth = 1;
        add(new JLabel("User Name:"), gbc);
        gbc.gridx = 1;
        add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(loginButton, gbc);

        loginButton.addActionListener(e -> checkLogin());
    }

    private void checkLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.equals(AppConfig.DEFAULT_USERNAME) &&
                password.equals(AppConfig.DEFAULT_PASSWORD)) {
            callback.onLoginSuccess();
            clearFields();
        } else {
            callback.onLoginFailed();
        }
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
    }
}