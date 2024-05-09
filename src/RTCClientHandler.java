import java.io.IOException;
import java.net.Socket;

public class RTCClientHandler extends ClientHandler{

    public RTCClientHandler(Socket socket, PlantModel plant){super(socket,plant);}

    @Override
    public void run() {
        String messageFromClient;

        while(socket.isConnected()){
            try{
                messageFromClient = bufferedReader.readLine();
                double value = parseMessageFromClient(messageFromClient);

                if (value==1){
                    plant.setHeatingOn(true);
                }else
                    plant.setHeatingOn(false);

                double roomTemp = plant.getRoomTemperature();
                bufferedWriter.write(roomTemp + "");

            }catch (IOException e){}
        }
    }

    private double parseMessageFromClient(String message){
        double value=0;
        try{
            value = Double.parseDouble(message);
        }catch (NumberFormatException e){}

        return value;
    }
}
