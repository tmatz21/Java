package ui;

import utils.Session;
import utils.UIFactory;

import javax.swing.*;
import java.awt.*;

public class MyApplications {

    public MyApplications(int userId) {
        if (userId > 0) {
            Session.userId = userId;
        }

        JFrame frame = new JFrame("My Applications");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = UIFactory.createAppPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        root.add(new MyApplicationsPanel(), BorderLayout.CENTER);

        frame.setContentPane(root);
        frame.setSize(920, 620);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
