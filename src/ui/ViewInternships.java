package ui;

import utils.UIFactory;

import javax.swing.*;
import java.awt.*;

public class ViewInternships {

    public ViewInternships() {
        JFrame frame = new JFrame("Internships");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = UIFactory.createAppPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        root.add(new ViewInternshipsPanel("student"), BorderLayout.CENTER);

        frame.setContentPane(root);
        frame.setSize(1080, 680);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}