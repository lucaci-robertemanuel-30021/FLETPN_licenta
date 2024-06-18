package Server.clientHandler;

import Server.models.PlantModel;

import java.io.IOException;
import java.net.Socket;

public class RTCClientHandler extends ClientHandler {

    public RTCClientHandler(Socket socket, PlantModel plant) {
        super(socket, plant);
    }

    @Override
    public void run() {
        String messageFromClient;

        try {
            messageFromClient = bufferedReader.readLine();
            while (socket.isConnected()) {


                boolean value = parseMessageFromClient(messageFromClient);

                if (value == true) {
                    plant.setHeatingOn(true);
                } else
                    plant.setHeatingOn(false);

                double roomTemp = plant.getRoomTemperature();
                bufferedWriter.write(roomTemp + "");

            }
        } catch (IOException e) {
        }
    }

    private boolean parseMessageFromClient(String message) {
        if (message.equals("true"))
            return true;
        else
            if(message.equals("false"))
            return false;

        return false;
    }
}
