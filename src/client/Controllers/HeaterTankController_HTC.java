package client.Controllers;

import client.ClientConstants;
import core.FuzzyPetriLogic.Executor.AsyncronRunnableExecutor;
import core.FuzzyPetriLogic.FuzzyDriver;
import core.FuzzyPetriLogic.FuzzyToken;
import core.FuzzyPetriLogic.PetriNet.FuzzyPetriNet;
import core.FuzzyPetriLogic.PetriNet.Recorders.FullRecorder;
import core.FuzzyPetriLogic.Tables.OneXOneTable;
import core.TableParser;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class HeaterTankController_HTC {


    static String reader = "" +
            "{[<NL><NM><ZR><PM><PL>]" +
            " [<NL><NM><ZR><PM><PL>]" +
            " [<NL><NM><ZR><PM><PL>]" +
            " [<NL><NM><ZR><PM><PL>]" +
            " [<NL><NM><ZR><PM><PL>]}";

    static String doubleChannelDifferentiator = ""//
            + "{[<ZR,ZR><NM,NM><NL,NL><NL,NL><NL,NL>]" //
            + " [<PM,PM><ZR,ZR><NM,NM><NL,NL><NL,NL>]" //
            + " [<PL,PL><PM,PM><ZR,ZR><NM,NM><NL,NL>]"//
            + " [<PL,PL><PL,PL><PM,PM><ZR,ZR><NM,NM>]"//
            + " [<PL,PL><PL,PL><PL,PL><PM,PM><ZR,ZR>]}";

    String historyMerger = ""//
            + "{[<NL,NL><NL,NL><NL,NL><NM,NM><ZR,ZR>]" //
            + " [<NL,NL><NL,NL><NM,NM><ZR,ZR><PM,PM>]" //
            + " [<NL,NL><NM,NM><ZR,ZR><PM,PM><PL,PL>]"//
            + " [<NM,NM><ZR,ZR><PM,PM><PL,PL><PL,PL>]"//
            + " [<ZR,ZR><PM,PM><PL,PL><PL,PL><PL,PL>]}";

    private AsyncronRunnableExecutor executor;
    private FullRecorder rec;
    private FuzzyDriver tankWaterTemperatureDriver;
    private int p1RefInp;
    private int p3SysInp;
    private FuzzyPetriNet net;
    private BufferedReader br;
    private PrintWriter pw;
    private boolean isRunning;

    public HeaterTankController_HTC(long simPeriod){
        createConnection();
        announceIdentity();
        createPetriNet_HTC(simPeriod);
    }
    public void createConnection(){
        try {
            Socket socket = new Socket(ClientConstants.Server_Address,ClientConstants.PORT);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            pw=new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void announceIdentity(){
        String controllerName = "HTC";
        pw.println(controllerName);
    }
    public void createPetriNet_HTC(long simPeriod) {
        // se construieste reteaua Petri pentru HTC component
        TableParser parser = new TableParser();
        net = new FuzzyPetriNet();
        int p0 = net.addPlace();
        net.setInitialMarkingForPlace(p0, FuzzyToken.zeroToken());
        // tranzitia t0 executa citirea
        int tr0Reader = net.addTransition(0, parser.parseTwoXOneTable(reader));
        p1RefInp = net.addInputPlace();
        net.addArcFromPlaceToTransition(p0, tr0Reader, 1.0);
        net.addArcFromPlaceToTransition(p1RefInp, tr0Reader, 1.0);
        int p2 = net.addPlace();
        net.addArcFromTransitionToPlace(tr0Reader, p2);
        p3SysInp = net.addInputPlace();
        // tranzitia t1 - diferentiator
        int tr1Diff = net.addTransition(0, parser.parseTwoXTwoTable(doubleChannelDifferentiator));
        net.addArcFromPlaceToTransition(p2, tr1Diff, 1.0);
        net.addArcFromPlaceToTransition(p3SysInp, tr1Diff, 1.0);
        int p4 = net.addPlace();
        net.addArcFromTransitionToPlace(tr1Diff, p4);
        // tranzitia t2 de iesire
        int tr2Out = net.addOuputTransition(OneXOneTable.defaultTable());
        int p5 = net.addPlace();
        net.addArcFromTransitionToPlace(tr1Diff, p5);
        // tranzitia t3 cu delay
        int t3 = net.addTransition(1, OneXOneTable.defaultTable());
        net.addArcFromPlaceToTransition(p5, t3, 1.0);
        net.addArcFromTransitionToPlace(t3, p0);
        int p6 = net.addPlace();
        net.setInitialMarkingForPlace(p6, FuzzyToken.zeroToken());
        // tranzitia t4 sumator
        int t4Adder = net.addTransition(0, parser.parseTwoXTwoTable(historyMerger));
        net.addArcFromPlaceToTransition(p4, t4Adder, 1.2);
        net.addArcFromPlaceToTransition(p6, t4Adder, 1.0);
        int p7 = net.addPlace();
        net.addArcFromTransitionToPlace(t4Adder, p7);
        net.addArcFromPlaceToTransition(p7, tr2Out, 1.0);

        int p8 = net.addPlace();
        net.addArcFromTransitionToPlace(t4Adder, p8);
        int t5Delay = net.addTransition(1, OneXOneTable.defaultTable());
        net.addArcFromPlaceToTransition(p8, t5Delay, 1.0);
        net.addArcFromTransitionToPlace(t5Delay, p6);
        //se specifica limitele pentru fuzzyficare
        FuzzyDriver tankCommandDriver = FuzzyDriver.createDriverFromMinMax(-1.0, 1.0);
        // se specifica limitele pentru apa din boiler
        tankWaterTemperatureDriver = FuzzyDriver.createDriverFromMinMax(-75, 75);
        rec = new FullRecorder();
        //se creaza executorul
        executor = new AsyncronRunnableExecutor(net, simPeriod);
        executor.setRecorder(rec);
        //se adauga o actiune tranzitiei de iesire t2 – comanda pentru gaz
        net.addActionForOuputTransition(tr2Out, new Consumer<FuzzyToken>() {
            @Override
            public void accept(FuzzyToken tk) {
                //plantModel.setHeaterGasCmd(tankCommandDriver.defuzzify(tk));
                    pw.write(String.valueOf(tankCommandDriver.defuzzify(tk)));
            }

        });
    }
    public void start() {    (new Thread(executor)).start();
    isRunning=true;
    new Thread(new Runnable() {
        @Override
        public void run() {
            while(isRunning){
               Double tankWaterTemperature = null;
               try{
                   tankWaterTemperature = Double.parseDouble(br.readLine());
                   setWaterRefTemp(75);
                   setTankWaterTemp(tankWaterTemperature);
               }catch (IOException e){
                   throw new RuntimeException(e);
               }
            }

        }
    }).start();}

    public void stop() { isRunning = false;
       // pw.println("stop HTC");
        executor.stop();  }
    // citire temperatura din boiler
    public void setTankWaterTemp(double tankWaterTemperature) {
        Map<Integer, FuzzyToken> inps = new HashMap<Integer, FuzzyToken>();
        inps.put(p3SysInp, tankWaterTemperatureDriver.fuzzifie(tankWaterTemperature));
        executor.putTokenInInputPlace(inps);  }
    // citire referinta pentru temperatura din boiler
    public void setWaterRefTemp(double waterRefTemp){

        Map<Integer, FuzzyToken> inps = new HashMap<Integer, FuzzyToken>();
        inps.put(p1RefInp, tankWaterTemperatureDriver.fuzzifie(waterRefTemp));
        executor.putTokenInInputPlace(inps);  }
    //metode pentru vizualizarea retelei Petri
    public FuzzyPetriNet getNet() {    return net;  }

    public FullRecorder getRecorder() {    return rec;  }
}
