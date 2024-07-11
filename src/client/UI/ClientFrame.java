package client.UI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

public class ClientFrame extends JFrame{
    private JPanel mainPanel;
    private JFrame frame = new JFrame();
    private JButton exitButton;
    private JButton HTC_btn;
    private JButton RTC_btn;
    private JButton ACC_btn;
    private String title = "";
    private Socket socket;

    public ClientFrame(){

        this.setSize(600,400);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setContentPane(getMainPanel());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(frame, "Ești sigur că dorești să ieși din aplicație", "Confirmă Ieșirea", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        HTC_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                HtcFrame htcWindow = new HtcFrame("HTC");
                htcWindow.setVisible(true);}

        });
        RTC_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                RtcFrame rtcWindow = new RtcFrame("RTC");
                rtcWindow.setVisible(true);
            }
        });
        ACC_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AccFrame accWindow = new AccFrame("ACC");
                accWindow.setVisible(true);
            }
        });

    }
   public ClientFrame(String title){
        this.setTitle(title);
    }

public JPanel getMainPanel(){

        return mainPanel;
}

    public static void main(String[] args) {
        ClientFrame clientFrame = new ClientFrame();
    }
}
