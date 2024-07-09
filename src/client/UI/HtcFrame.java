package client.UI;

import Main.FuzzyPVizualzer;
import Main.Plotter;
import Server.ServerConstants;
import View.MainView;
import client.ClientConstants;
import client.Controllers.HeaterTankController_HTC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class HtcFrame extends JFrame implements Runnable {
    private JPanel HtcPanel;
    private JButton turnOnOffButton;
    private JButton goBackButton;
    private JTextArea performanteTextArea;
    private boolean buttonIsPressed = false;
    private PrintWriter printWriter = null;
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

                HeaterTankController_HTC htc= new HeaterTankController_HTC(ClientConstants.SIM_PERIOD);

                if (!buttonIsPressed) {
                    turnOnOffButton.setBackground(Color.GREEN);
                    buttonIsPressed = true;

                    try {
                        System.out.println("ceva");
                        Socket socket = new Socket(ClientConstants.Server_Address, ClientConstants.PORT);
                        System.out.println("ceva2");
                        printWriter = new PrintWriter(
                                new BufferedWriter(new OutputStreamWriter(
                                        socket.getOutputStream())),true);
                        String controllerName ="HTC";
                        printWriter.println(controllerName);
                        printWriter.flush();
                        System.out.println("pornitt");
                        performanteTextArea.append("HTC este pornit\n");
                        htc.start();
                        double waterRefTemp = 75.0;
                     /*
                        MainView windowTankController = FuzzyPVizualzer.visualize(htc.getNet(),htc.getRecorder());
                        Plotter plotterTemperatureLog = new Plotter(plantModel.getTemperatureLogs());
                        Plotter plotterCommandLog = new Plotter(plantModel.getCommandLogs());
                        windowTankController.addInteractivePanel("TempLogs", plotterTemperatureLog.makeInteractivePlot());
                        windowTankController.addInteractivePanel("ComandLogs", plotterCommandLog.makeInteractivePlot());

                       double[] tankTempStats = HtcFrame.calcStatistics(plantModel.getTemperatureLogs().get("tankTemp"));
                       performanteTextArea.append("Temperatura maxima apa: "+tankTempStats[0]+"\n");
                       performanteTextArea.append("Temperatura minima apa: "+tankTempStats[1]+"\n");
                       performanteTextArea.append("Temperatura medie apa: "+tankTempStats[2]+"\n");
                       banda = tankTempStats[2]*5/100;
                       Tstationar = tankTempStats[2]-banda;
                       performanteTextArea.append("Banda de eroare +-5% față de temp medie a apei: +-"
                       stApa= (Tstationar-tankTempStats[1])*100/tankTempStats[2];
                       performanteTextArea.append("Variatia temp apei față de valoarea medie: "+stApa+"%\n");
*/
                    } catch (IOException ee) {
                        performanteTextArea.append("Eroare: "+ee.getMessage()+"\n");
                        ee.printStackTrace();
                    }

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
