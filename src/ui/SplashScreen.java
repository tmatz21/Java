package ui;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {

    public SplashScreen() {
        setFocusableWindowState(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Title
        JLabel title = new JLabel("Internship Management System", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        // Loading text
        JLabel loading = new JLabel("Loading...", SwingConstants.CENTER);
        loading.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Progress bar
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        panel.add(title, BorderLayout.NORTH);
        panel.add(loading, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);

        add(panel);

        setSize(400, 200);
        setLocationRelativeTo(null);
    }

    public void showSplash() {
        setVisible(true);

        Timer timer = new Timer(2000, e -> {
            setVisible(false);
            dispose();

            // Open login after splash
            new LoginForm();
        });
        timer.setRepeats(false);
        timer.start();
    }
}