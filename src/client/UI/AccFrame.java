package client.UI;

import Server.ServerConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class AccFrame extends JFrame{
    private JFrame acCFrame = new JFrame();
    private JPanel AccPanel;
    private JButton turnACCOnOffButton;
    private JButton goBackButton;
    private JTextArea performanteTextArea;
    private boolean buttonIsPressed = false;
    private BufferedWriter bufferedWriter = null;

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
          if (!buttonIsPressed) {
            turnACCOnOffButton.setBackground(Color.GREEN);
            buttonIsPressed = true;
            try {
                Socket socket = new Socket(ServerConstants.Server_Address, ServerConstants.PORT);
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                String controllerName = "ACC";
                bufferedWriter.write(controllerName);
                bufferedWriter.flush();
                performanteTextArea.append("ACC este pornit\n");


            } catch (IOException ee) {
                performanteTextArea.append("Eroare: " + ee.getMessage() + "\n");
                ee.printStackTrace();
            }

        } else {
            turnACCOnOffButton.setBackground(Color.RED);
            buttonIsPressed = false;
            performanteTextArea.append("ACC este oprit\n");
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
}
