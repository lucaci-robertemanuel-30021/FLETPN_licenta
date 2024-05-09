package UI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;

public class RtcFrame extends JFrame {
    private JFrame rtCFrame = new JFrame();
    private JButton goBackButton;
    private JButton turnOnOffButton;
    private JPanel RtcPanel;
    private JTextField textField1;
    private boolean buttonIsPressed = false;
    public RtcFrame(){
        turnOnOffButton.setBackground(Color.RED);
        this.setSize(600,400);
        this.setLocationRelativeTo(null);
        this.setContentPane(getRtcPanel());
        this.setVisible(true);
        goBackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                   setVisible(false);
            }
        });

        turnOnOffButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!buttonIsPressed) {
                    turnOnOffButton.setBackground(Color.GREEN);
                    buttonIsPressed = true;
                } else {
                    turnOnOffButton.setBackground(Color.RED);
                    buttonIsPressed = false;
                }
            }
        });
    }
    public RtcFrame(String title){
        this();
        this.setTitle(title);
    }

    public JPanel getRtcPanel(){

        return RtcPanel;
    }
}
