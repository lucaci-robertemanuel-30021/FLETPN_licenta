package Server.clientHandler;

import Server.models.PlantModel;

import java.io.*;
import java.net.Socket;

public abstract class ClientHandler implements Runnable {
    public Socket socket;
    public BufferedReader bufferedReader;
    public BufferedWriter bufferedWriter;
    public PlantModel plant;

    public ClientHandler(Socket socket, PlantModel plant) {
       try {
           this.plant = plant;
           this.socket = socket;
           this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
           this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

       }catch (IOException e){

       }

    }

    public abstract void run(); // to be implemented in subclasses
}

