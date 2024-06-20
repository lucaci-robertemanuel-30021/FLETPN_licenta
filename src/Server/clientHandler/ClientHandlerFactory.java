package Server.clientHandler;

import Server.models.PlantModel;

import java.net.Socket;

public class ClientHandlerFactory {
    private PlantModel plant;
    public String controllerName;

    public ClientHandlerFactory(PlantModel plant){
        this.plant = plant;
    }

    public ClientHandler getClientHandler(Socket socket, String controllerName){
        System.out.println("Controller " + controllerName + " has connected");

        if (controllerName.equals("HTC")) {
            setControllerName(controllerName);
            return new HTCClientHandler(socket, plant);
        }else
        if (controllerName.equals("RTC")){
            setControllerName(controllerName);
            return new RTCClientHandler(socket, plant);
        }else
        if(controllerName.equals("ACC")){
            setControllerName(controllerName);
            return  new ACCClientHandler(socket,plant);
        }
        else
        {
            System.out.println("Wrong controller name");
        }
        return null;
    }

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }
}
