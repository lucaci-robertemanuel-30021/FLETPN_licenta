import java.io.IOException;
import java.net.Socket;

public class ACCClientHandler extends ClientHandler{

    public ACCClientHandler(Socket socket, PlantModel plant){super(socket,plant);}

    @Override
    public void run() {
        String messageFromClient;

        while(socket.isConnected()){
            try{
                messageFromClient = bufferedReader.readLine();
                double value = parseMessageFromClient(messageFromClient);

                // cum fac aici ca am si setACOn si setIsCool
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
