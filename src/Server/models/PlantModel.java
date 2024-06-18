package Server.models;

import java.util.*;

public class PlantModel {
    private volatile boolean ACOn = false;
    private volatile boolean isCool = false;
    private volatile boolean heaterOn = false;
    private volatile double gasCmd = 0.0;
    private int tickCntr = 0;
    private long period;
    private RoomModel room;
    private Scenario scenario;
    private HeaterTank tank;
    private AirConditionerModel ac;
    private double AcAirTemp = 22;

    /* for logs */
    ArrayList<Double> heaterWaterTempLog = new ArrayList<>();
    ArrayList<Double> roomTempLog = new ArrayList<>();
    ArrayList<Double> waterHeaterCmdLog = new ArrayList<>();
    ArrayList<Double> heatOnCmdLog = new ArrayList<>();
    //////
   ArrayList<Double> acAirTempLog = new ArrayList<>();
   ArrayList<Double> AcOnLog = new ArrayList<>();
    ArrayList<Double> CoolOnCmdLog = new ArrayList<>();
//
// ////
    int heatOnCntr = 0;
    int ACOnCntr = 0;
    int continuousHeatOnMax = 0;
    int continuousHeatOnCurrent = 0;
    double tankGasCommandSum = 0.0;
    int continuousAcOnCurrent = 0;
    int continuousAcOnHeatMax = 0;
    int continuousAcOnCoolMax = 0;

    public PlantModel(long simPeriod, Scenario scen) {
        this.period = simPeriod;
        room = new RoomModel();
        tank = new HeaterTank();
        ac = new AirConditionerModel();
        scenario = scen;  }

    public void setHeatingOn(boolean heaterOn) {this.heaterOn = heaterOn;  }
    ///////////////
    public void setACOn(boolean ACOn) { this.ACOn = ACOn;  }
    public void setIsCool(boolean isCool){
        this.isCool = isCool;
    }
    public void setAcAirTemp(double acAirTemp){this.AcAirTemp=acAirTemp;}
    /////////////////
    public void setHeaterGasCmd(double cmd) {
        gasCmd = cmd;  }

    public double getRoomTemperature() {
        return room.getCurrentTemperature();  }

    public double heatingOnRatio() {
        return ((double) heatOnCntr / (double) tickCntr);  }
    public double ACOnRatio() {
        return ((double) ACOnCntr / (double) tickCntr);  }

    public double gasConsumption() {
        return tankGasCommandSum;  }

    public int maxContinuousHeaterOn() {
        return continuousHeatOnMax;  }


    public void start() {
        Timer myTimer = new Timer();
        TimerTask task = new TimerTask() {
            //updates the information sent every set "period" until the scenario is completed
            @Override
            public void run() {
                if (tickCntr < scenario.getScenarioLength()) {
                    tank.updateSystem(heaterOn, gasCmd);
                    ac.updateModel(ACOn, isCool, AcAirTemp);
                    room.updateModel(heaterOn, tank.getHotWaterTemperature(), scenario.getWindowOpen(tickCntr),
                            scenario.getOutSideTemperature(tickCntr), ac.getCurrentAirTemperature(),ACOn,isCool);
                    makeLogs();
                    tickCntr++;
                }
                else {
                    myTimer.cancel();
                    myTimer.purge();        }
            }
        };
        myTimer.scheduleAtFixedRate(task, period, period);  }

    public void makeLogs() {
        heaterWaterTempLog.add(tank.getHotWaterTemperature());
        roomTempLog.add(room.getCurrentTemperature());
        waterHeaterCmdLog.add(gasCmd);
        heatOnCmdLog.add(heaterOn ? 1.0 : 0.0);
        heatOnCntr += (heaterOn ? 1.0 : 0.0);
        ACOnCntr += (ACOn ? 1.0 : 0.0);
        if (heaterOn) {
            continuousHeatOnCurrent++;
        }
        else if (continuousHeatOnCurrent > 0) {
            if (continuousHeatOnCurrent > continuousHeatOnMax) {
                continuousHeatOnMax = continuousHeatOnCurrent;
            }
            continuousHeatOnCurrent = 0;
        }
        tankGasCommandSum += (gasCmd < 0.0) ? 0.0 : gasCmd;
        /////*

        acAirTempLog.add(ac.getCurrentAirTemperature());
        AcOnLog.add(ACOn ? 1.0 : 0.0);
        CoolOnCmdLog.add(isCool ? 1.0 : 0.0);
        if(ACOn==false){
            continuousAcOnCurrent = 0;
        }else if(isCool==false){ //ac on is true
            if(continuousAcOnCurrent > 0){
                if(continuousAcOnCurrent > continuousAcOnHeatMax){
                    continuousAcOnHeatMax = continuousAcOnCurrent;
                }
            }else{//is cool true
                if(continuousHeatOnCurrent > continuousAcOnCoolMax){
                    continuousAcOnCoolMax = continuousAcOnCurrent;
                }
            }
            continuousAcOnCurrent = 0;
        }
    }
    public Double getTankWaterTemperature() {
        return tank.getHotWaterTemperature();  }

    public Map<String, List<Double>> getTemperatureLogs() {
        HashMap<String, List<Double>> logMap = new HashMap<>();
        logMap.put("tankTemp", heaterWaterTempLog);
        logMap.put("roomTemp", roomTempLog);
        logMap.put("acAirTemp", acAirTempLog);
        return logMap;  }

    public Map<String, List<Double>> getCommandLogs() {
        HashMap<String, List<Double>> logMap = new HashMap<>();
        logMap.put("waterCmd", waterHeaterCmdLog);
        logMap.put("heaterOn", heatOnCmdLog);
        logMap.put("AcOn",AcOnLog);
        logMap.put("isCool",CoolOnCmdLog);
        return logMap;  }

}
