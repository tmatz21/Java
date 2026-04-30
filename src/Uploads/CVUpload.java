package Uploads;

import utils.UIFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class CVUpload {

    public CVUpload() {
        JFrame frame = new JFrame("Upload CV");

        JButton uploadBtn = UIFactory.createPrimaryButton("Upload CV");
        JLabel fileLabel = new JLabel("No file selected");

        uploadBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(frame);

            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                fileLabel.setText(file.getAbsolutePath());
            }
        });

        frame.setLayout(new FlowLayout());
        frame.add(uploadBtn);
        frame.add(fileLabel);

        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}