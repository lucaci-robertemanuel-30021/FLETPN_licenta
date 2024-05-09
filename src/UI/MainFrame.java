package UI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame{
    private JPanel mainPanel;
    private JFrame frame = new JFrame();
    private JButton exitButton;
    private JButton HTC_btn;
    private JButton RTC_btn;
    private JButton ACC_btn;
    private JComboBox comboBox1;
    private String title = "";
    public MainFrame(){
        this.setSize(600,400);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setContentPane(getMainPanel());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit?", "Confirm Exit", JOptionPane.YES_NO_OPTION);
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
   public MainFrame(String title){
        this();
        this.setTitle(title);

    }
public JPanel getMainPanel(){

        return mainPanel;
}


}
