import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

private ServerSocket serverSocket;
private InputStreamReader inputStreamreader = null;
//private OutputStreamWriter outputStreamWriter = null;
private BufferedReader bufferedReader = null;
//private BufferedWriter bufferedWriter = null;
private List<ClientHandler> connectedControllers = new ArrayList<>();
public Server(ServerSocket serverSocket){
    this.serverSocket = serverSocket;
}


public void startPlant(){

    try{
        //here start plant & scenario
        Scenario scenario = Scenario.winterDay();
        System.out.println("winter day");
        PlantModel plantModel = new PlantModel(Constants.SIM_PERIOD, scenario);
        double waterRefTemp = 75.0;
        double roomTemperature = 24.0;
        ClientHandlerFactory factory = new ClientHandlerFactory(plantModel);

        while(!serverSocket.isClosed()) {
            Socket socket = serverSocket.accept();

            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String controllerName = bufferedReader.readLine();
            //problem is elements in the connectedControllers are null
            ClientHandler c = factory.getClientHandler(socket, controllerName);
            Thread thread = new Thread(c);
            thread.start();

            connectedControllers.add(c);

            // print list of controllers
            System.out.println("Connected controllers: ");
            for(int i=0;i<connectedControllers.size();i++){
                System.out.println(connectedControllers.get(i));
              //  System.out.println(factory.getControllerName());
            }

            //aici pornesc controllerele NU

            //dupa asta sa fac si daca se deconecteaza un controller
            //connectedControllers.remove(c);
            //System.out.println("Controller deconectat");
        }
    }catch (IOException e){

    }
}

public void closeServerSocket(){
    try{
        if(serverSocket != null){
            serverSocket.close();
        }
    }catch (IOException e){
        e.printStackTrace();
    }
}


    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(Constants.PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Server server = new Server(serverSocket);
        server.startPlant();
        System.out.println("main finish");
    }

}
