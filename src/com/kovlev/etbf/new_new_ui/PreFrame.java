package com.kovlev.etbf.new_new_ui;

import com.kovlev.etbf.util.ETBFSException;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class PreFrame extends JFrame {

    private ArrayList<String> paths;

    public PreFrame() throws Exception {
        super("Encrypted Tag Based FileSystem file selection");

        String configfilename = System.getProperty("user.home") + "/.etbfs.dat";
        if (Files.exists(Paths.get(configfilename))) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(configfilename));
            paths = (ArrayList<String>) ois.readObject();
            System.out.println(paths.size());
            ois.close();
        } else {
            paths = new ArrayList<>();
        }

        JButton openButton = new JButton("Open");
        openButton.addActionListener(actionEvent -> {
            JFileChooser jfc = new JFileChooser();
            int returnValue = jfc.showOpenDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                try {
                    String name = jfc.getSelectedFile().getAbsolutePath();
                    paths.add(name);
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(configfilename));
                    oos.writeObject(paths);
                    oos.close();
                    open(name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        setPreferredSize(new Dimension(500, 500));

        JList list = new JList(paths.toArray());

        list.addListSelectionListener(listSelectionEvent -> {
            if (!listSelectionEvent.getValueIsAdjusting()) {
                int idx = list.getSelectedIndex();
                if (idx != -1) {
                    try {
                        open(paths.get(idx));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    list.clearSelection();
                }
            }
        });

        JButton createButton = new JButton("Create");
        createButton.addActionListener(actionEvent -> {

        });


        JPanel panel = new JPanel();
        panel.add(openButton);
        panel.add(createButton);
        add(panel, BorderLayout.NORTH);
        add(list, BorderLayout.CENTER);

        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void open(String filename) throws Exception {
        String password = JOptionPane.showInputDialog(new JFrame(), "Password");
        try {
            new MainFrame(filename, password);
            dispose();
        } catch (ETBFSException e) {
            JOptionPane.showMessageDialog(new JFrame(), "Exception: " + e);
        }

    }
}
