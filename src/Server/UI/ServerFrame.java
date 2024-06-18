package Server.UI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerFrame extends JFrame{
    private JComboBox comboBox1;
    private JButton startPlantButton;
    private JFrame frame = new JFrame();
    private JTextArea logField;
    private JPanel mainPanel;
    private JButton ieșireButton;

    public ServerFrame() {
        this.setSize(600,400);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setContentPane(getMainPanel());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    startPlantButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //start the plant with selected scenario

            //start serversocket(ce face clasa Server sau apelez)
        }
    });
        ieșireButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(frame, "Ești sigur că dorești să ieși din aplicație", "Confirmă Ieșirea", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    public ServerFrame(String title){
        this();
        this.setTitle(title);
    }

    public JPanel getMainPanel(){

        return mainPanel;
    }
}
