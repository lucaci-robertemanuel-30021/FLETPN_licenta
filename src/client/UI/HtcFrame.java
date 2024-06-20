package client.UI;

import Server.ServerConstants;
import client.ClientConstants;
import client.Controllers.HeaterTankController_HTC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class HtcFrame extends JFrame {
    private JPanel HtcPanel;
    private JButton turnOnOffButton;
    private JButton goBackButton;
    private JTextArea performanteTextArea;
    private boolean buttonIsPressed = false;
    private BufferedWriter bufferedWriter = null;
    private BufferedReader bufferedReader = null;
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

                    try {
                        Socket socket = new Socket(ServerConstants.Server_Address, ServerConstants.PORT);
                        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        String controllerName = "HTC";
                        bufferedWriter.write(controllerName);
                        bufferedWriter.flush();
                        performanteTextArea.append("HTC este pornit\n");
                        //ar trb sa il pornesc din afara de undeva ca sa il pot opri dupa?
                        HeaterTankController_HTC htc= new HeaterTankController_HTC(ClientConstants.SIM_PERIOD);
                        htc.start();
                        double waterRefTemp = 75.0;
                        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        System.out.println(bufferedReader);

                       /* for (int i = 0; i < scenario.getScenarioLength(); i++) {
                            htc.setWaterRefTemp(waterRefTemp);
                            htc.setTankWaterTemp(plantModel.getTankWaterTemperature());

                            try {Thread.sleep(10);
                            } catch (InterruptedException eee) { eee.printStackTrace();	}
                        }
*/
                    } catch (IOException ee) {
                        performanteTextArea.append("Eroare: "+ee.getMessage()+"\n");
                        ee.printStackTrace();
                    }

                } else {
                    turnOnOffButton.setBackground(Color.RED);
                    buttonIsPressed = false;
                    performanteTextArea.append("HTC este oprit\n");
                    //htc.stop();
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
