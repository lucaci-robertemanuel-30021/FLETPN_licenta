package Server.models;

public class AirConditionerModel {
    //something similar like heater tank
    private static final double StartingTemperature = 23.0;
    private static final double maxAirTemperature = 24.0;
    private static final double minAirTemperature = 10.0;

    double currentAirTemperature;

    public AirConditionerModel() {
        currentAirTemperature = StartingTemperature;
    }

    public double getCurrentAirTemperature() {
        return currentAirTemperature;
    }

    public void updateModel(boolean AC_on, boolean isCold,double AcAirTemp) {

        //assures that air temp is between [min,max]
     //   AcAirTemp = (AcAirTemp < minAirTemperature) ? minAirTemperature :
     //           ((AcAirTemp > maxAirTemperature) ? maxAirTemperature : AcAirTemp);
       // double error = (currentAirTemperature-AcAirTemp);
        AcAirTemp = (AcAirTemp < 0.0) ? 0.0 : ((AcAirTemp > 1.0) ? 1.0 : AcAirTemp);
        if(AC_on){
            if(isCold){
                currentAirTemperature-=AcAirTemp*0.0004*(maxAirTemperature-currentAirTemperature);
            }
            else
                currentAirTemperature+=AcAirTemp*0.0004*(-currentAirTemperature+minAirTemperature);
        }else
            currentAirTemperature=StartingTemperature;

    }
}
