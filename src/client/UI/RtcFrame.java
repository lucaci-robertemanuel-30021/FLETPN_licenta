package client.UI;

import client.ClientConstants;
import client.Controllers.RoomTemperatureController_RTC;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
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
                    try {
                        Socket socket = new Socket(ClientConstants.Server_Address, ClientConstants.PORT);
                        printWriter = new PrintWriter(socket.getOutputStream(),true);
                        String controllerName = "RTC";
                        printWriter.println(controllerName);
                        rtc.start();

                        //de luat din text field instead
                        double roomTemperature = 24.0;
                      /*

                        MainView windowTermostat = FuzzyPVizualzer.visualize(rtc.getNet(), rtc.getRecorder());
                        Plotter plotterTemperatureLog = new Plotter(plantModel.getTemperatureLogs());
                        Plotter plotterCommandLog = new Plotter(plantModel.getCommandLogs());
                        windowTermostat.addInteractivePanel("TempLogs", plotterTemperatureLog.makeInteractivePlot());
                        windowTermostat.addInteractivePanel("ComandLogs", plotterCommandLog.makeInteractivePlot());

                        double[] roomTempStats = RtcFrame.calcStatistics(plantModel.getTemperatureLogs().get("roomTemp"));
                        performanteTextArea.append("Temperatura maxima camera: "+roomTempStats[0]+"\n");
                        performanteTextArea.append("Temperatura minima camera: "+roomTempStats[1]+"\n");
                        performanteTextArea.append("Temperatura medie camera: "+roomTempStats[2]+"\n");
                        stCamera= (roomTempStats[0]-roomTempStats[1])*100/roomTempStats[2];
                        performanteTextArea.append("Variatia temp față de valoarea medie: "+stCamera+"%\n");
                        performanteTextArea.append("Raport incalzire pornita: "+plantModel.heatingOnRatio+"\n");
                        performanteTextArea.append("Numar maxim centrala pornita: "+plantModel.maxContinuousHeaterOn+"\n");
                        performanteTextArea.append("Consum total: " + plantModel.gasConsumption()+"\n");
                        performanteTextArea.append("Consum mediu intr-un minut" + plantModel.gasConsumption() / scenario.getScenarioLength()+"\n");

                     */
                    } catch (IOException ee2) {
                        performanteTextArea.append("Eroare: " + ee2.getMessage() + "\n");
                        ee2.printStackTrace();
                    }

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
