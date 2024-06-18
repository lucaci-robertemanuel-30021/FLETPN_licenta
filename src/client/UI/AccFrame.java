package client.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AccFrame extends JFrame{
    private JFrame acCFrame = new JFrame();
    private JPanel AccPanel;
    private JButton turnACCOnOffButton;
    private JButton goBackButton;
    private JTextArea textArea1;
    private boolean buttonIsPressed = false;
public AccFrame() {
    turnACCOnOffButton.setBackground(Color.RED);
    this.setSize(600,400);
    this.setLocationRelativeTo(null);
    this.setContentPane(getAccPanel());
    this.setVisible(true);
    goBackButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
        }
    });
    turnACCOnOffButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!buttonIsPressed) {
                turnACCOnOffButton.setBackground(Color.GREEN);
                buttonIsPressed = true;
            } else {
                turnACCOnOffButton.setBackground(Color.RED);
                buttonIsPressed = false;
            }
        }
    });
}
    public AccFrame(String title){
        this();
        this.setTitle(title);
    }

    public JPanel getAccPanel(){

        return AccPanel;
    }
}
