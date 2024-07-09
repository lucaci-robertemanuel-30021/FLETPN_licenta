package Server.UI;

import Server.Server;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerFrame extends JFrame{
    private JComboBox scenarioSelecter;
    private JButton startPlantButton;
    private JFrame frame = new JFrame();
    private JTextArea logField;
    private JPanel mainPanel;
    private JButton iesireButton;
    String selectedScenario;

    public ServerFrame(Server server) {
        this.setSize(600,400);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setContentPane(getMainPanel());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    startPlantButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            server.startPlant(selectedScenario);

            logField.append("Model centrala pornit\n");
            (new Thread() {
                public void run() {
                    server.startServerSocket();
                }
            }).start();
        }
    });
        iesireButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(frame, "Ești sigur că dorești să ieși din aplicație", "Confirmă Ieșirea", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        scenarioSelecter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedScenario = (String) scenarioSelecter.getSelectedItem();

                logField.append("Scenariul selectat este: "+selectedScenario+"\n");
                scenarioSelecter.setEnabled(false);
            }
        });
    }
    public ServerFrame(Server server,String title){
        this(server);
        this.setTitle(title);
    }

    public JPanel getMainPanel(){

        return mainPanel;
    }

}
