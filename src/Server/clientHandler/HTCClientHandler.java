package Server.clientHandler;

import Server.models.PlantModel;

import java.io.IOException;
import java.net.Socket;

public class HTCClientHandler extends ClientHandler {

    public HTCClientHandler(Socket socket, PlantModel plant) {super(socket, plant);}

    public void run() {
        String messageFromClient;
        try {
            messageFromClient = bufferedReader.readLine();
        while (socket.isConnected()) {
                double value = parseMessageFromClient(messageFromClient);
                plant.setHeaterGasCmd(value);
                double waterTemp = plant.getTankWaterTemperature();
                bufferedWriter.write(waterTemp + "");
            }} catch (IOException e) {
            e.printStackTrace();}
    }

    private double parseMessageFromClient(String message){
        double value=0;
        try{
            value = Double.parseDouble(message);
        }catch (NumberFormatException e){}
        return value;}
}

