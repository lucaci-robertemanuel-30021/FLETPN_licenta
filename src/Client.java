import View.MainView;
import Main.FuzzyPVizualzer;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import Main.Plotter;
public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private String name;

    public Client(Socket socket, String name) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.name = name;
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage() {
        try {
            bufferedWriter.write(name);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(name + ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msg;

                while (socket.isConnected()) {
                    try {
                        msg = bufferedReader.readLine();
                        System.out.println(msg);
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);

                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {

        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Add the name of the controller you want to use(ACC, HTC, RTC): ");
        String name = scanner.nextLine();
        Socket socket = null;

            try {
                socket = new Socket(Constants.Server_Address, Constants.PORT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            InputStreamReader isr;
            OutputStreamWriter osw;
            try {
                isr = new InputStreamReader(socket.getInputStream());
                osw = new OutputStreamWriter(socket.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            BufferedReader bufferedR = new BufferedReader(isr);
            BufferedWriter bufferedW = new BufferedWriter(osw);


            try {
                bufferedW.write(name);
                bufferedW.newLine();
                bufferedW.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        Client client = new Client(socket, name);
        client.listenForMessage();
        client.sendMessage();
       // client.closeEverything(socket, bufferedR, bufferedW);
    }
    }

