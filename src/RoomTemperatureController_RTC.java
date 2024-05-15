import core.FuzzyPetriLogic.Executor.AsyncronRunnableExecutor;
import core.FuzzyPetriLogic.FuzzyDriver;
import core.FuzzyPetriLogic.FuzzyToken;
import core.FuzzyPetriLogic.PetriNet.FuzzyPetriNet;
import core.FuzzyPetriLogic.PetriNet.Recorders.FullRecorder;
import core.FuzzyPetriLogic.Tables.OneXOneTable;
import core.TableParser;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class RoomTemperatureController_RTC {
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

    static String t3Table = "{[<FF,ZR>, <FF,FF>, <FF,FF>, <FF,FF>, <ZR, FF>]}";

    private int p1RefInp;
    private FuzzyPetriNet net;
    private FuzzyDriver temperatureDriver;
    private FullRecorder rec;
    private AsyncronRunnableExecutor executor;
    private int p3RealInp;
    //prin constructor primeste perioada referinta pentru plant
    public RoomTemperatureController_RTC(OutputStreamWriter osw, long simPeriod) {
        net = new FuzzyPetriNet();
        TableParser parser = new TableParser();

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
        net.addArcFromTransitionToPlace(t1, p4);
        int t2 = net.addTransition(1, OneXOneTable.defaultTable());
        net.addArcFromPlaceToTransition(p4, t2, 1.0);
        net.addArcFromTransitionToPlace(t2, p0);

        int p5 = net.addPlace();
        net.addArcFromTransitionToPlace(t1, p5);
        int t3 = net.addTransition(0, parser.parseOneXTwoTable(t3Table));
        int p6 = net.addPlace();
        net.addArcFromTransitionToPlace(t3, p6);
        int t4 = net.addOuputTransition(OneXOneTable.defaultTable());
        net.addArcFromPlaceToTransition(p6, t4, 1.0);
        int p7 = net.addPlace();
        net.addArcFromTransitionToPlace(t3, p7);
        int t5 = net.addOuputTransition(OneXOneTable.defaultTable());
        net.addArcFromPlaceToTransition(p7, t5, 1.0);
        net.addArcFromPlaceToTransition(p5, t3, 120.0);

        net.addActionForOuputTransition(t4, new Consumer<FuzzyToken>() {

            @Override
            public void accept(FuzzyToken t) {
              //  plantModel.setHeatingOn(true);
                try {
                    osw.write("true");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        net.addActionForOuputTransition(t5, new Consumer<FuzzyToken>() {

            @Override
            public void accept(FuzzyToken t) {
                //plantModel.setHeatingOn(false);
                try {
                    osw.write("false");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        temperatureDriver = FuzzyDriver.createDriverFromMinMax(-40, 40);

        rec = new FullRecorder();
        executor = new AsyncronRunnableExecutor(net, simPeriod);
        executor.setRecorder(rec);
    }
    public void start() {    (new Thread(executor)).start();  }

    public void stop() {    executor.stop();  }

    public void setInput(double roomTemperatureRef, double roomTemperature) {
        Map<Integer, FuzzyToken> inps = new HashMap<Integer, FuzzyToken>();
        inps.put(p1RefInp, temperatureDriver.fuzzifie(roomTemperatureRef));
        inps.put(p3RealInp, temperatureDriver.fuzzifie(roomTemperature));
        executor.putTokenInInputPlace(inps);
    }
    public FuzzyPetriNet getNet() {    return net;  }

    public FullRecorder getRecorder() {    return rec;  }

}
