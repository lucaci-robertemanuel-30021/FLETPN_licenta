package client.UI;

import Server.ServerConstants;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class RtcFrame extends JFrame {
    //de introdus aici
    //private client.Controllers.RoomTemperatureController_RTC rtc;
    private JFrame rtCFrame = new JFrame();
    private JButton goBackButton;
    private JButton turnOnOffButton;
    private JPanel RtcPanel;
    private JTextField textField1;
    private JTextArea performanteTextArea;
    private boolean buttonIsPressed = false;
    private BufferedWriter bufferedWriter = null;
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
                if (!buttonIsPressed) {
                    turnOnOffButton.setBackground(Color.GREEN);
                    buttonIsPressed = true;
                    try {
                        Socket socket = new Socket(ServerConstants.Server_Address, ServerConstants.PORT);
                        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        String controllerName = "RTC";
                        bufferedWriter.write(controllerName);
                        bufferedWriter.flush();
                        performanteTextArea.append("RTC este pornit\n");

                        // new client.Controllers.RoomTemperatureController_RTC(client.ClientConstants.SIM_PERIOD);

                    } catch (IOException ee) {
                        performanteTextArea.append("Eroare: " + ee.getMessage() + "\n");
                        ee.printStackTrace();
                    }

                } else {
                    turnOnOffButton.setBackground(Color.RED);
                    buttonIsPressed = false;
                    performanteTextArea.append("RTC este oprit\n");
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
