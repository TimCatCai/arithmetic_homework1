package timcat;

import java.util.Stack;

public class Main {
    public static void main(String [] args){
        RiverCrossing riverCrossing = new RiverCrossing();
        riverCrossing.crossing();
        System.out.println("=============================");
        System.out.println("过桥问题求解：");
        BridgeClossing bridgeClossing = new BridgeClossing();
        bridgeClossing.crossing();
    }
}
