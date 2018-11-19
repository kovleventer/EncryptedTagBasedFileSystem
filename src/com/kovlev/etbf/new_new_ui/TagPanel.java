package com.kovlev.etbf.new_new_ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class TagPanel extends JPanel {
    private JTextField textField;
    private JButton addButton;
    private JPanel lower;
    private FileListModel flm;

    private ArrayList<String> getTagStrings() {
        ArrayList<String> list = new ArrayList<>();
        for (Component c : lower.getComponents()) {
            TagEntry entry = (TagEntry)c;
            list.add(entry.getText());
        }
        return list;
    }

    public TagPanel(FileListModel flm) {
        super();
        this.flm = flm;
        setLayout(new BorderLayout());
        JPanel upper = new JPanel();
        textField = new JTextField(20);
        addButton = new JButton("Add");
        upper.add(textField);
        upper.add(addButton);
        add(upper, BorderLayout.NORTH);

        lower = new JPanel();
        lower.setLayout(new FlowLayout());
        addButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!"".equals(textField.getText())) {
                    lower.add(new TagEntry(textField.getText()));
                    flm.updateSelection(getTagStrings());
                    textField.setText("");
                    TagPanel.this.repaint();
                    TagPanel.this.revalidate();
                }
            }
        });
        add(lower, BorderLayout.SOUTH);
    }

    private class TagEntry extends JLabel {
        public TagEntry(String s) {
            super(s);
            setOpaque(true);
            setForeground(Color.WHITE);
            setBackground(Color.BLUE);
            setFont(new Font("Sans", Font.BOLD, 14));
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent mouseEvent) {
                    lower.remove(TagEntry.this);
                    flm.updateSelection(getTagStrings());
                    textField.setText("");
                    TagPanel.this.repaint();
                    TagPanel.this.revalidate();
                }

                @Override
                public void mousePressed(MouseEvent mouseEvent) { }

                @Override
                public void mouseReleased(MouseEvent mouseEvent) { }

                @Override
                public void mouseEntered(MouseEvent mouseEvent) { }

                @Override
                public void mouseExited(MouseEvent mouseEvent) { }
            });
        }
    }
}
