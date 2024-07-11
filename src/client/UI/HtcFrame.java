package client.UI;

import client.ClientConstants;
import client.Controllers.HeaterTankController_HTC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class HtcFrame extends JFrame implements Runnable {
    private JPanel HtcPanel;
    private JButton turnOnOffButton;
    private JButton goBackButton;
    private JTextArea performanteTextArea;
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

                HeaterTankController_HTC htc = new HeaterTankController_HTC(ClientConstants.SIM_PERIOD);

                if (!buttonIsPressed) {
                    turnOnOffButton.setBackground(Color.GREEN);
                    buttonIsPressed = true;

                        performanteTextArea.append("HTC este pornit\n");

                    htc.start();

                } else {
                    turnOnOffButton.setBackground(Color.RED);
                    buttonIsPressed = false;
                    performanteTextArea.append("HTC este oprit\n");
                    htc.stop();
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

    @Override
    public void run() {

    }
}
