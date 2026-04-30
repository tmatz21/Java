package ui;

import utils.UITheme;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {

        // Run UI safely on Swing thread
        SwingUtilities.invokeLater(() -> {

            // Use the system look and feel for more reliable native focus behavior.
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Improve font globally
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 14));
            UIManager.put("Button.background", UITheme.PRIMARY_DARK);
            UIManager.put("Button.foreground", UITheme.TEXT_LIGHT);
            UIManager.put("Button.select", UITheme.PRIMARY);

            // Launch login directly to avoid splash screen focus issues.
            new LoginForm();
        });
    }
}