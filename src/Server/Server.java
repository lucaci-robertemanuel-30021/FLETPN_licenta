package Server;

import Main.FuzzyPVizualzer;
import Main.Plotter;
import Server.UI.ServerFrame;
import Server.clientHandler.ClientHandler;
import Server.clientHandler.ClientHandlerFactory;
import Server.models.PlantModel;
import Server.models.Scenario;
import View.MainView;
import client.ClientConstants;
import client.Controllers.AirConditionerController_ACC;
import client.Controllers.HeaterTankController_HTC;
import client.Controllers.RoomTemperatureController_RTC;
import client.UI.AccFrame;
import client.UI.HtcFrame;
import client.UI.RtcFrame;

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

            //aici pentru conectare regulatoare
            for(int i=0;i<connectedControllers.size();i++){
                System.out.println(connectedControllers.get(i));
                if(connectedControllers.get(i).toString().contains("HTC")){
                    System.out.println("  HTC  ");

                    //aici trebuie sa trimit cumva prin socket?
                   /* InputStream is = socket.getInputStream();
                    ObjectInputStream ois = new ObjectInputStream(is);
                        try {
                            ois.readObject();
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    HeaterTankController_HTC htc = ois; */
                    HeaterTankController_HTC htc= new HeaterTankController_HTC(ClientConstants.SIM_PERIOD);
                    htc.createPetriNet_HTC(ClientConstants.SIM_PERIOD);

                    MainView windowTankController = FuzzyPVizualzer.visualize(htc.getNet(),htc.getRecorder());
                    Plotter plotterTemperatureLog = new Plotter(plantModel.getTemperatureLogs());
                    Plotter plotterCommandLog = new Plotter(plantModel.getCommandLogs());
                    windowTankController.addInteractivePanel("TempLogs", plotterTemperatureLog.makeInteractivePlot());
                    windowTankController.addInteractivePanel("ComandLogs", plotterCommandLog.makeInteractivePlot());

                    double[] tankTempStats = HtcFrame.calcStatistics(plantModel.getTemperatureLogs().get("tankTemp"));
                    System.out.println("Temperatura maxima apa: "+tankTempStats[0]);
                    System.out.println("Temperatura minima apa: "+tankTempStats[1]);
                    System.out.println("Temperatura medie apa: "+tankTempStats[2]);
                    double banda = tankTempStats[2]*5/100;
                    double Tstationar = tankTempStats[2]-banda;
                    System.out.println("Banda de eroare +-5% față de temp medie a apei: +- "+Tstationar);
                    double stApa= (Tstationar-tankTempStats[1])*100/tankTempStats[2];
                    System.out.println("Variatia temp apei față de valoarea medie: "+stApa);
                }
                if(connectedControllers.get(i).toString().contains("RTC")){
                    System.out.println("  RTC  ");

                    RoomTemperatureController_RTC rtc = new RoomTemperatureController_RTC(ClientConstants.SIM_PERIOD);
                    rtc.createPetriNet_RTC(ClientConstants.SIM_PERIOD);

                        MainView windowTermostat = FuzzyPVizualzer.visualize(rtc.getNet(), rtc.getRecorder());
                        Plotter plotterTemperatureLog = new Plotter(plantModel.getTemperatureLogs());
                        Plotter plotterCommandLog = new Plotter(plantModel.getCommandLogs());
                        windowTermostat.addInteractivePanel("TempLogs", plotterTemperatureLog.makeInteractivePlot());
                        windowTermostat.addInteractivePanel("ComandLogs", plotterCommandLog.makeInteractivePlot());

                        double[] roomTempStats = RtcFrame.calcStatistics(plantModel.getTemperatureLogs().get("roomTemp"));
                    System.out.println("Temperatura maxima camera: "+roomTempStats[0]+"\n");
                    System.out.println("Temperatura minima camera: "+roomTempStats[1]+"\n");
                    System.out.println("Temperatura medie camera: "+roomTempStats[2]+"\n");
                        double stCamera= (roomTempStats[0]-roomTempStats[1])*100/roomTempStats[2];
                    System.out.println("Variatia temp față de valoarea medie: "+stCamera+"%\n");
                    System.out.println("Raport incalzire pornita: "+plantModel.heatingOnRatio()+"\n");
                    System.out.println("Numar maxim centrala pornita: "+plantModel.maxContinuousHeaterOn()+"\n");
                    System.out.println("Consum total: " + plantModel.gasConsumption()+"\n");
                    System.out.println("Consum mediu intr-un minut" + plantModel.gasConsumption() / scenario.getScenarioLength()+"\n");

                }
                if(connectedControllers.get(i).toString().contains("ACC")){
                    System.out.println("  ACC  ");

                    AirConditionerController_ACC acc = new AirConditionerController_ACC(ClientConstants.SIM_PERIOD);
                    acc.createPetriNet_ACC(ClientConstants.SIM_PERIOD);

                MainView windowAirConditioner = FuzzyPVizualzer.visualize(acc.getNet(), acc.getRecorder());
                Plotter plotterTemperatureLog = new Plotter(plantModel.getTemperatureLogs());
                Plotter plotterCommandLog = new Plotter(plantModel.getCommandLogs());
                windowAirConditioner.addInteractivePanel("TempLogs", plotterTemperatureLog.makeInteractivePlot());
                windowAirConditioner.addInteractivePanel("ComandLogs", plotterCommandLog.makeInteractivePlot());

                double[] ACTempStats = AccFrame.calcStatistics(plantModel.getTemperatureLogs().get("acAirTemp"));
                    System.out.println("Temperatura maxima aer: " + ACTempStats[0]+"\n");
                    System.out.println("Temperatura minima aer: " + ACTempStats[1]+"\n");
                    System.out.println("Temperatura medie aer: " + ACTempStats[2]+"\n");
                    System.out.println("Raport aer conditionat pornit: " + plantModel.ACOnRatio()+"\n");
                double stAer= (ACTempStats[0]-ACTempStats[1])*100/ACTempStats[2];
                    System.out.println("Variatia temp aerului față de valoarea medie: "+stAer+"%\n");


                }
            }
            //aici pentru deconectare regulatoare
            for(int i=0;i<connectedControllers.size();i++){
            if(bufferedReader.readLine().toString().contains("stop HTC")){
                System.out.println(bufferedReader.readLine());
                connectedControllers.remove(i);
                System.out.println("Controller HTC deconectat");
            }
                if(bufferedReader.readLine().toString().contains("stop RTC")){

                    connectedControllers.remove(i);
                    System.out.println("Controller RTC deconectat");
                }
                if(bufferedReader.readLine().toString().contains("stop ACC")){

                    connectedControllers.remove(i);
                    System.out.println("Controller ACC deconectat");
                }
            }

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
