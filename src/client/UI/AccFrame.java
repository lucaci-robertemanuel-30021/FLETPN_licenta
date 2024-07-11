package client.UI;

import client.ClientConstants;
import client.Controllers.AirConditionerController_ACC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AccFrame extends JFrame{
    private JFrame acCFrame = new JFrame();
    private JPanel AccPanel;
    private JButton turnACCOnOffButton;
    private JButton goBackButton;
    private JTextArea performanteTextArea;
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
            AirConditionerController_ACC acc = new AirConditionerController_ACC(ClientConstants.SIM_PERIOD);
            if (!buttonIsPressed) {
            turnACCOnOffButton.setBackground(Color.GREEN);
            buttonIsPressed = true;
            performanteTextArea.append("ACC este oprit\n");
            acc.start();


        } else {
            turnACCOnOffButton.setBackground(Color.RED);
            buttonIsPressed = false;
            performanteTextArea.append("ACC este oprit\n");
            acc.stop();
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

    public static double[] calcStatistics(List<Double> list) {
        double min = 1000.0;
        double max = 0.0;
        double sum = 0.0;
        for (Double d : list) {
            min = (min > d) ? d : min;
            max = (max < d) ? d : max;
            sum += d;		}
        return new double[] { max, min, sum / list.size() };
    }
}
