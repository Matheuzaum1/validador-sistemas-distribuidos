package com.newpix.test;

import javax.swing.*;
import java.awt.*;

public class TestGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Teste de GUI - NewPix");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);
            
            JLabel label = new JLabel("Se voce esta vendo isso, a GUI funciona!", JLabel.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            frame.add(label);
            
            frame.setVisible(true);
            
            System.out.println("Janela de teste criada e exibida!");
        });
    }
}
