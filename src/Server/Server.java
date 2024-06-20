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

    public void startPlant(){
    try {
        Socket socket = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String scenarioName = reader.readLine();
        System.out.println(reader.readLine());
        System.out.println("Server: " + scenarioName);
        startScenario(scenarioName);

        PlantModel plantModel = new PlantModel(ServerConstants.SIM_PERIOD, scenario);
        setPlantModel(plantModel);

        System.out.println("Model centrală pornită");
    } catch (IOException e) {
        throw new RuntimeException(e);}}
    public void startServerSocket(){
        ClientHandlerFactory factory = new ClientHandlerFactory(getPlantModel());
        try{
        while(!serverSocket.isClosed()) {   ///while-ul asta blocheaza executia
            Socket socket = serverSocket.accept();
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String controllerName = bufferedReader.readLine();
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Server server = new Server(serverSocket);
        ServerFrame serverFrame = new ServerFrame(server,"Plant");
        System.out.println("main finish");
    }

}
