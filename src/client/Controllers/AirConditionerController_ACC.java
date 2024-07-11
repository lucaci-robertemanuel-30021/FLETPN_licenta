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

public class AirConditionerController_ACC {
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

    static String t4Table = "{[<FF,ZR>, <FF,FF>, <FF,FF>, <FF,FF>, <ZR, FF>]}";

    static String reader1X2 = "{[<NL,NL><NM,NM><ZR,ZR><PM,PM><PL,PL>]}"; //t3

    static String simple_t5_rule = "{[<PL,FF><PL,FF><FF,FF><FF,NL><FF,NL> ]}";
    //////////

    private FuzzyPetriNet net;
    private int p1RefInp;
    private int p3RealInp;
    private FuzzyDriver temperatureDriver;
    private FullRecorder rec;
    private AsyncronRunnableExecutor executor;
    private BufferedReader br;
    private PrintWriter pw;
    private boolean isRunning;

    public AirConditionerController_ACC(long simPeriod){
        createConnection();
        announceIdentity();
        createPetriNet_ACC(simPeriod);
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
        String controllerName = "ACC";
        pw.println(controllerName);
    }
    public void createPetriNet_ACC(long simPeriod) {
        net = new FuzzyPetriNet();
        TableParser parser=new TableParser();

        int p0 = net.addPlace();
        net.setInitialMarkingForPlace(p0, FuzzyToken.zeroToken());
        p1RefInp = net.addInputPlace();

        int t0 = net.addTransition(0, parser.parseTwoXOneTable(reader));
        net.addArcFromPlaceToTransition(p0, t0, 1.0);
        net.addArcFromPlaceToTransition(p1RefInp, t0, 1.0);
        int p2 = net.addPlace();
        net.addArcFromTransitionToPlace(t0, p2);
        p3RealInp = net.addInputPlace();

        int t1 = net.addTransition(0, parser.parseTwoXTwoTable(doubleChannelDifferentiator));
        net.addArcFromPlaceToTransition(p2, t1, 1.0);
        net.addArcFromPlaceToTransition(p3RealInp, t1, 1.0);

        int p4 = net.addPlace();
        int p5 = net.addPlace();
        net.addArcFromTransitionToPlace(t1, p4);
        net.addArcFromTransitionToPlace(t1, p5);
        int t2 = net.addTransition(1, OneXOneTable.defaultTable());
        net.addArcFromPlaceToTransition(p4, t2, 1.0);
        net.addArcFromTransitionToPlace(t2, p0);

        int t3 = net.addTransition(0, parser.parseOneXTwoTable(reader1X2));
        net.addArcFromPlaceToTransition(p5, t3, 1.0);
        int p6 = net.addPlace();
        int p7 = net.addPlace();
        net.addArcFromTransitionToPlace(t3, p6);
        net.addArcFromTransitionToPlace(t3, p7);

        int t4 = net.addTransition(0, parser.parseOneXTwoTable(t4Table));
        net.addArcFromPlaceToTransition(p6, t4, 120.0);
        int p8 = net.addPlace();
        int p11 = net.addPlace();
        net.addArcFromTransitionToPlace(t4, p8);
        net.addArcFromTransitionToPlace(t4, p11);
        int t6Out = net.addOuputTransition(OneXOneTable.defaultTable());
        int t7Out = net.addOuputTransition(OneXOneTable.defaultTable());
        net.addArcFromPlaceToTransition(p11, t6Out, 1.0);
        net.addArcFromPlaceToTransition(p8, t7Out, 1.0);

        int t5 = net.addTransition(0, parser.parseOneXTwoTable(simple_t5_rule));
        net.addArcFromPlaceToTransition(p7, t5, 1.0);
        int p9 = net.addPlace();
        int p10 = net.addPlace();
        net.addArcFromTransitionToPlace(t5, p9);
        net.addArcFromTransitionToPlace(t5, p10);
        int t8Out = net.addOuputTransition(OneXOneTable.defaultTable());
        int t9Out = net.addOuputTransition(OneXOneTable.defaultTable());
        net.addArcFromPlaceToTransition(p9, t8Out, 1.0);
        net.addArcFromPlaceToTransition(p10, t9Out, 1.0);

        temperatureDriver = FuzzyDriver.createDriverFromMinMax(-40, 40);

        rec = new FullRecorder();
        executor = new AsyncronRunnableExecutor(net, simPeriod);
        executor.setRecorder(rec);

        net.addActionForOuputTransition(t6Out, new Consumer<FuzzyToken>() {
            @Override
            public void accept(FuzzyToken fuzzyToken) {
                //plantModel.setACOn(true);
                    pw.write("Otrue");

            }
        });
        net.addActionForOuputTransition(t7Out, new Consumer<FuzzyToken>() {
            @Override
            public void accept(FuzzyToken fuzzyToken) {
                //plantModel.setACOn(false);
                   pw.write("Ofalse");
            }
        });
        net.addActionForOuputTransition(t8Out, new Consumer<FuzzyToken>() {
            @Override
            public void accept(FuzzyToken fuzzyToken) {
                //plantModel.setIsCool(false); //false is heat
                    pw.write("Cfalse");
            }
        });
        net.addActionForOuputTransition(t9Out, new Consumer<FuzzyToken>() {
            @Override
            public void accept(FuzzyToken fuzzyToken) {
                //plantModel.setIsCool(true); // true is cool
                    pw.write("Ctrue");
            }
        });

    }
    public void start() {    (new Thread(executor)).start();
    isRunning = true;
    new Thread(new Runnable() {
        @Override
        public void run() {
            while(isRunning){
                Double roomTemperature = null;
                try{
                    roomTemperature=Double.parseDouble(br.readLine());
                    setInput(24, roomTemperature);
                }catch (IOException e){
                    throw new RuntimeException(e);
                }
            }
        }
    }).start();}

    public void stop() {isRunning=false;
        executor.stop();  }

    public void setInput(double roomTemperatureRef, double roomTemperature) {
        Map<Integer, FuzzyToken> inps = new HashMap<Integer, FuzzyToken>();
        inps.put(p1RefInp, temperatureDriver.fuzzifie(roomTemperatureRef));
        inps.put(p3RealInp, temperatureDriver.fuzzifie(roomTemperature));

        executor.putTokenInInputPlace(inps);
    }
    public FuzzyPetriNet getNet() {    return net;  }

    public FullRecorder getRecorder() {    return rec;  }
}
