package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HtcFrame extends JFrame {
    private JPanel HtcPanel;
    private JButton turnOnOffButton;
    private JButton goBackButton;
    private boolean buttonIsPressed = false;
    public HtcFrame(){
        turnOnOffButton.setBackground(Color.RED);
        this.setSize(600,400);
        this.setLocationRelativeTo(null);
        this.setContentPane(getHtcPanel());
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
                    //creeaz
                } else {
                    turnOnOffButton.setBackground(Color.RED);
                    buttonIsPressed = false;
                }
            }

        });
    }

    public HtcFrame(String title){
        this();
        this.setTitle(title);
    }

    public JPanel getHtcPanel(){

        return HtcPanel;
    }
}
