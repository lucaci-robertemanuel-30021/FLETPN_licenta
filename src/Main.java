import java.io.OutputStreamWriter;
import java.util.List;

import client.Controllers.AirConditionerController_ACC;
import client.Controllers.HeaterTankController_HTC;
import client.Controllers.RoomTemperatureController_RTC;
import Main.FuzzyPVizualzer;
import Main.Plotter;
import client.UI.ClientFrame;
import View.MainView;
import Server.models.PlantModel;
import Server.models.Scenario;

public class Main {
    private static final int SIM_PERIOD = 10;

    public static void main(String[] args) {
        //for gui
       // ClientFrame clientFrame = new ClientFrame("Plant");
        ///////////
        OutputStreamWriter osw=null;
        Scenario scenario = Scenario.winterDay();
        System.out.println("winter day");
        PlantModel plantModel = new PlantModel(SIM_PERIOD, scenario);
        HeaterTankController_HTC tankController = new HeaterTankController_HTC(SIM_PERIOD); //changed plantModel to osw to all 3
        RoomTemperatureController_RTC roomController = new RoomTemperatureController_RTC(SIM_PERIOD);
        AirConditionerController_ACC airConditionerController = new AirConditionerController_ACC(SIM_PERIOD);

        roomController.start();
        tankController.start();
        airConditionerController.start();
        plantModel.start();
        double waterRefTemp = 75.0;
        double roomTemperature = 24.0;

        for (int i = 0; i < scenario.getScenarioLength(); i++) {
            tankController.setWaterRefTemp(waterRefTemp);
            tankController.setTankWaterTemp(plantModel.getTankWaterTemperature());
            roomController.setInput(roomTemperature, plantModel.getRoomTemperature());
            airConditionerController.setInput(roomTemperature, plantModel.getRoomTemperature());

            try {Thread.sleep(10);
            } catch (InterruptedException e) { e.printStackTrace();	}
        }
        tankController.stop();
        roomController.stop();
        airConditionerController.stop();

        MainView windowTankController = FuzzyPVizualzer.visualize(tankController.getNet(),
                tankController.getRecorder());
        MainView windowTermostat = FuzzyPVizualzer.visualize(roomController.getNet(), roomController.getRecorder());
        MainView windowAirConditioner = FuzzyPVizualzer.visualize(airConditionerController.getNet(), airConditionerController.getRecorder());

        Plotter plotterTemperatureLog = new Plotter(plantModel.getTemperatureLogs());
        Plotter plotterCommandLog = new Plotter(plantModel.getCommandLogs());
        windowTankController.addInteractivePanel("TempLogs", plotterTemperatureLog.makeInteractivePlot());
        windowTermostat.addInteractivePanel("TempLogs", plotterTemperatureLog.makeInteractivePlot());
        windowAirConditioner.addInteractivePanel("TempLogs", plotterTemperatureLog.makeInteractivePlot());
        windowTankController.addInteractivePanel("ComandLogs", plotterCommandLog.makeInteractivePlot());
        windowTermostat.addInteractivePanel("ComandLogs", plotterCommandLog.makeInteractivePlot());
        windowAirConditioner.addInteractivePanel("ComandLogs", plotterCommandLog.makeInteractivePlot());

        double[] tankTempStats = Main.calcStatistics(plantModel.getTemperatureLogs().get("tankTemp"));
        double[] roomTempStats = Main.calcStatistics(plantModel.getTemperatureLogs().get("roomTemp"));
        double[] ACTempStats = Main.calcStatistics(plantModel.getTemperatureLogs().get("acAirTemp"));
        System.out.println("max tank temp :" + tankTempStats[0]);
        System.out.println("min tank temp :" + tankTempStats[1]);
        System.out.println("avg tank temp :" + tankTempStats[2]);
        System.out.println("max room temp :" + roomTempStats[0]);
        System.out.println("min room temp :" + roomTempStats[1]);
        System.out.println("avg room temp :" + roomTempStats[2]);
        System.out.println("heater on ratio:" + plantModel.heatingOnRatio());
        System.out.println("max nr of minutes continuous heating on:" + plantModel.maxContinuousHeaterOn());
        //added to update the gas consumption
        //plant.makeLogs();
        System.out.println("all consumption ::" + plantModel.gasConsumption());
        System.out.println("avg consumption in  a min ::" + plantModel.gasConsumption() / scenario.getScenarioLength());

        System.out.println("max ac air temp :" + ACTempStats[0]);
        System.out.println("min ac air temp :" + ACTempStats[1]);
        System.out.println("avg ac air temp :" + ACTempStats[2]);
        System.out.println("AC on ratio:" + plantModel.ACOnRatio());
    }

    public static double[] calcStatistics(List<Double> list) {
        double min = 1000.0;
        double max = 0.0;
        double sum = 0.0;
        for (Double d : list) {
            min = (min > d) ? d : min;
            max = (max < d) ? d : max;
            sum += d;		}
        return new double[] { max, min, sum / list.size() };
    }
}
