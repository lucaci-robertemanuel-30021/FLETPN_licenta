package Server.clientHandler;

import Server.models.PlantModel;

import java.io.IOException;
import java.net.Socket;

public class ACCClientHandler extends ClientHandler {

    public ACCClientHandler(Socket socket, PlantModel plant){super(socket,plant);}

    @Override
    public void run() {
        String messageFromClient;

        while(socket.isConnected()){
            try{
                messageFromClient = bufferedReader.readLine();
                String value = parseMessageFromClient(messageFromClient);

                if(value.contains("O")){
                  if(value.contains("true")){
                      plant.setACOn(true);
                  }else if (value.contains("false")){
                      plant.setACOn(false);
                  }
                    if(value.contains("C")){
                        if(value.contains("true")){
                            plant.setIsCool(true);
                        }else if (value.contains("false")){
                            plant.setIsCool(false);
                        }}

                }

            }catch (IOException e){}
        }
    }
    //   O-onOff sau C-cool/heat inainte  true/false
    private String parseMessageFromClient(String message){

        if(message.equals("Cfalse")){
            return message;
        }else if(message.equals("Ctrue")){
            return message;
        }
        if(message.equals("Otrue")){
            return message;
        }else if(message.equals("Ofalse")){
            return message;
        }
        return "Wrong message";
    }
}
