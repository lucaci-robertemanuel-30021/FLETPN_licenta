package Server;

import Server.UI.ServerFrame;
import client.UI.ClientFrame;
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
    private final Object lock = new Object();

private ServerSocket serverSocket;
private InputStreamReader inputStreamreader = null;
//private OutputStreamWriter outputStreamWriter = null;
private BufferedReader bufferedReader = null;
//private BufferedWriter bufferedWriter = null;
private List<ClientHandler> connectedControllers = new ArrayList<>();
private Scenario scenario;
    String scenarioValue;
public Server(ServerSocket serverSocket){
    this.serverSocket = serverSocket;
}

//wait and receive Scneario
    public void receiveScenario(String scenario){
        System.out.println("Received scenario: "+scenario);
        synchronized (lock){
            this.scenarioValue = scenario;
            lock.notify();
        }
    }

    public void waitForScenario(){
        synchronized (lock){
            while(scenarioValue == null){
                try{
                    lock.wait();
                }catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }
            }
        }
        System.out.println("Selected Scenario server 1: "+scenarioValue);

        if(scenarioValue.equals("Winter Day")){
            scenario=Scenario.winterDay();

        }else if(scenarioValue.equals("Summer Day")){
            scenario=Scenario.summerDay();

        }else if(scenarioValue.equals("Autumn Day")){
            scenario=Scenario.autumnDay();

        }else if(scenarioValue.equals("Spring Day")){
            scenario=Scenario.springDay();

        }else if(scenarioValue.equals("Winter Morning")){
            scenario=Scenario.winterMorning();
        }

    }
public void startPlant(){

    try{

        //here start plant & scenario
        //////
        receiveScenario(scenarioValue);
        System.out.println("Selected Scenario server 2: "+ scenarioValue);
        PlantModel plantModel = new PlantModel(ServerConstants.SIM_PERIOD, scenario);
        System.out.println("PLant started");
        //////
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

        ServerFrame serverFrame = new ServerFrame("Plant");
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(ServerConstants.PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Server server = new Server(serverSocket);
        server.startPlant();
        System.out.println("main finish");
    }

}
