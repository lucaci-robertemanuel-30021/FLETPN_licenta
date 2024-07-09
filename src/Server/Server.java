package Server;

import Server.UI.ServerFrame;
import Server.clientHandler.ClientHandler;
import Server.clientHandler.ClientHandlerFactory;
import Server.models.PlantModel;
import Server.models.Scenario;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

private ServerSocket serverSocket;
private BufferedReader bufferedReader = null;

private List<ClientHandler> connectedControllers = new ArrayList<>();
private Scenario scenario;
private PlantModel plantModel;

public Server(ServerSocket serverSocket){
    this.serverSocket = serverSocket;
}

    public void startScenario(String scenarioValue){

        if(scenarioValue.equals("Zi de iarna")){
            scenario=Scenario.winterDay();

        }else if(scenarioValue.equals("Zi de vara")){
            scenario=Scenario.summerDay();

        }else if(scenarioValue.equals("Zi de toamna")){
            scenario=Scenario.autumnDay();

        }else if(scenarioValue.equals("Zi de primavara")){
            scenario=Scenario.springDay();

        }else if(scenarioValue.equals("Dimineata de iarna")){
            scenario=Scenario.winterMorning();
        }
    }

    public PlantModel getPlantModel() {
        return plantModel;
    }

    public void setPlantModel(PlantModel plantModel) {
        this.plantModel = plantModel;
    }

    public void startPlant(String selectedScenario){

        startScenario(selectedScenario);

        PlantModel plantModel = new PlantModel(ServerConstants.SIM_PERIOD, scenario);
        setPlantModel(plantModel);

        System.out.println("Model centrală pornită");
}

    public void startServerSocket(){
        ClientHandlerFactory factory = new ClientHandlerFactory(getPlantModel());
        String controllerName=null;
        try{
        while(!serverSocket.isClosed()) {

            Socket socket = serverSocket.accept();
            System.out.println("Port is: "+serverSocket.getLocalPort());
            System.out.println("Address is: "+serverSocket.getLocalSocketAddress());
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("before");
            bufferedReader.readLine();
            System.out.println("after");
            //problema e ca controller name ramane mereu null

            while(controllerName==null){
                System.out.println("of");
                controllerName = bufferedReader.readLine(); //
                if(controllerName==null){
                    System.out.println("nul");
                }
                System.out.println(controllerName);}

            System.out.println("controller name: "+controllerName);
            ClientHandler c = factory.getClientHandler(socket, controllerName);
            Thread thread = new Thread(c);
            thread.start();
            connectedControllers.add(c);
            System.out.println("Connected controllers: ");

            for(int i=0;i<connectedControllers.size();i++){
                System.out.println(connectedControllers.get(i));
            }
            //dupa asta sa fac si daca se deconecteaza un controller
            //connectedControllers.remove(c);
            //System.out.println("Controller deconectat");

             }
    }catch (IOException e){}
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
            serverSocket = new ServerSocket(ServerConstants.PORT);
            System.out.println("Server is started on port: "+ServerConstants.PORT);
            Server server = new Server(serverSocket);
            ServerFrame serverFrame = new ServerFrame(server,"Plant");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("main finish");
    }

}
