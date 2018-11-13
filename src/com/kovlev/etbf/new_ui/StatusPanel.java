package com.kovlev.etbf.new_ui;

import javax.swing.*;
import java.awt.*;

public class StatusPanel extends JPanel {
    private JLabel label;

    public StatusPanel(Controller c) {
        super();
        setBackground(Color.GREEN);
        label = new JLabel();
        add(label);
    }

    public void setStatus(String status) {
        label.setText(status);
    }
}
