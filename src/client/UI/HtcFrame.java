package client.UI;

import Server.ServerConstants;

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

                    } catch (IOException ee) {
                        performanteTextArea.append("Eroare: "+ee.getMessage()+"\n");
                        ee.printStackTrace();
                    }

                } else {
                    turnOnOffButton.setBackground(Color.RED);
                    buttonIsPressed = false;
                    performanteTextArea.append("HTC este oprit\n");
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
