package ui;

import utils.UIFactory;

import javax.swing.*;
import java.awt.*;

public class ViewApplications {

    public ViewApplications() {
        JFrame frame = new JFrame("Applications");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = UIFactory.createAppPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        root.add(new ViewApplicationsPanel(), BorderLayout.CENTER);

        frame.setContentPane(root);
        frame.setSize(980, 640);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}