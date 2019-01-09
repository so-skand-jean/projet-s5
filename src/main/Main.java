package main;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame app = new JFrame("App");
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setLayout(new FlowLayout());

        JButton afficherBtn = new JButton("Start");
        afficherBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Hello you!");
            }
        });
        app.add(afficherBtn);

        JButton afficherBtn2 = new JButton("Stop");
        afficherBtn2.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Hello you!");
            }
        });
        app.add(afficherBtn);
        
        app.pack();
        app.setVisible(true);
    }
}