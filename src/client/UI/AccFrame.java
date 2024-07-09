package client.UI;

import Main.FuzzyPVizualzer;
import Main.Plotter;
import Server.ServerConstants;
import View.MainView;
import client.ClientConstants;
import client.Controllers.AirConditionerController_ACC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class AccFrame extends JFrame{
    private JFrame acCFrame = new JFrame();
    private JPanel AccPanel;
    private JButton turnACCOnOffButton;
    private JButton goBackButton;
    private JTextArea performanteTextArea;
    private boolean buttonIsPressed = false;
    private PrintWriter printWriter = null;

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
            try {
                Socket socket = new Socket(ClientConstants.Server_Address, ClientConstants.PORT);
                printWriter = new PrintWriter(socket.getOutputStream(),true);
                String controllerName = "ACC";
                printWriter.println(controllerName);
                acc.start();
               /*

                MainView windowAirConditioner = FuzzyPVizualzer.visualize(acc.getNet(), acc.getRecorder());
                Plotter plotterTemperatureLog = new Plotter(plantModel.getTemperatureLogs());
                Plotter plotterCommandLog = new Plotter(plantModel.getCommandLogs());
                windowAirConditioner.addInteractivePanel("TempLogs", plotterTemperatureLog.makeInteractivePlot());
                windowAirConditioner.addInteractivePanel("ComandLogs", plotterCommandLog.makeInteractivePlot());

                double[] ACTempStats = AccFrame.calcStatistics(plantModel.getTemperatureLogs().get("acAirTemp"));
                performanteTextArea.append("Temperatura maxima aer: " + ACTempStats[0]+"\n");
                performanteTextArea.append("Temperatura minima aer: " + ACTempStats[1]+"\n");
                performanteTextArea.append("Temperatura medie aer: " + ACTempStats[2]+"\n");
                performanteTextArea.append("Raport aer conditionat pornit: " + plantModel.ACOnRatio()+"\n");
                stAer= (ACTempStats[0]-ACTempStats[1])*100/ACTempStats[2];
                performanteTextArea.append("Variatia temp aerului față de valoarea medie: "+stAer+"%\n");

                */
            } catch (IOException ee) {
                performanteTextArea.append("Eroare: " + ee.getMessage() + "\n");
                ee.printStackTrace();
            }

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
