package client;

import client.UI.ClientFrame;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(Socket socket) {

            this.socket = socket;
    }

    public void sendMessage() {
        try {
            bufferedWriter.newLine();
            bufferedWriter.flush();
            while (socket.isConnected()) {
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

        Socket socket = null;

            try {
                socket = new Socket(ClientConstants.Server_Address, ClientConstants.PORT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        Client client = new Client(socket);
        ClientFrame clientFrame = new ClientFrame(client,"Client");
       // client.listenForMessage();
    //   client.sendMessage();
    }
    }
