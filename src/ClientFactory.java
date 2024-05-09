import java.net.Socket;

public class ClientFactory {
    private PlantModel plant;

    public ClientFactory(PlantModel plant){
        this.plant = plant;
    }

    public ClientHandler getClientHandler(Socket socket, String controllerName){
        System.out.println("Controller " + controllerName + " has connected");

        if (controllerName == "HTC") {
            return new HTCClientHandler(socket, plant);
        }else
        if (controllerName == "RTC"){
            return new RTCClientHandler(socket, plant);
        }else
        if(controllerName == "ACC"){
            return  new ACCClientHandler(socket,plant);
        }
        //daca adaug aici chiar daca numele e bun tot se afiseaza wrong controller
       // else
     //   {
     //       System.out.println("Wrong controller name");
     //   }

        return null;
    }
}
