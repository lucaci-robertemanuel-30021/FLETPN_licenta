package Server.models;

public class RoomModel {
    private static final double StartingTemperature = 24.0;

    /* daca diferenta dintre apa incalzita si temperatura camerei este 1C atunci temperature camerei va urca cu <heaterConstant> in fiecare minut*/
    private static final double heaterConstant = 0.01;

    /* daca diferenta dintre temperature de afara si temperature camerei este 1C atunci temperature camerei va urca constant cu <wallConstant> in fiecare minut   */
    private static final double wallConstant = 0.00055;

    /*daca diferenta dintre temperature de afara si temperature camerei este de 1C atunci geamul este deschis si temperature va scadea cu <windowConstant> in fiecare minut   */
    private static final double windowConstant = 0.01;
    double currentTemperature;

    public RoomModel() { currentTemperature = StartingTemperature;  }

    public void updateModel(boolean heatingOn, double heaterWaterTemp, boolean windowOpen, double outSideTemp,double ACAirTemp, boolean ACOn, boolean isCold) {
        double deltaHeater = (heatingOn) ? (heaterWaterTemp - currentTemperature) : 0.0;
        double outsideDelta = currentTemperature - outSideTemp;
        double AirDelta;
        if(ACOn){
            if(isCold){
                AirDelta=ACAirTemp-currentTemperature;
            }
            else
                AirDelta=currentTemperature-ACAirTemp;
        }else
            AirDelta=StartingTemperature;
       // double AirDelta = (ACOn) ? (currentTemperature - ACAirTemp) : 0.0;
                               //c(k)     *      b
        currentTemperature += deltaHeater * heaterConstant + AirDelta*0.001 - outsideDelta * wallConstant -
                ((windowOpen) ? (outsideDelta * windowConstant) : 0.0);  }

    public double getCurrentTemperature() {
        return currentTemperature;  }

}
