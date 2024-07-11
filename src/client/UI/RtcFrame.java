package client.UI;

import client.ClientConstants;
import client.Controllers.RoomTemperatureController_RTC;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.io.PrintWriter;
import java.util.List;

public class RtcFrame extends JFrame {
    private JFrame rtCFrame = new JFrame();
    private JButton goBackButton;
    private JButton turnOnOffButton;
    private JPanel RtcPanel;
    private JTextField textField1;
    private JTextArea performanteTextArea;
    private boolean buttonIsPressed = false;
    private PrintWriter printWriter = null;
    public RtcFrame() {
        turnOnOffButton.setBackground(Color.RED);
        this.setSize(600, 400);
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
                RoomTemperatureController_RTC rtc = new RoomTemperatureController_RTC(ClientConstants.SIM_PERIOD);

                if (!buttonIsPressed) {
                    turnOnOffButton.setBackground(Color.GREEN);
                    buttonIsPressed = true;

                    performanteTextArea.append("RTC este pornit\n");
                    rtc.start();

                } else {
                    turnOnOffButton.setBackground(Color.RED);
                    buttonIsPressed = false;
                    performanteTextArea.append("RTC este oprit\n");
                    rtc.stop();
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
