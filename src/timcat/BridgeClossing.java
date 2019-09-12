package timcat;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import java.util.*;
import java.util.stream.IntStream;

public class BridgeClossing {

    public void crossing() {
//        SimpleWeightedGraph<BridgeStatus, DefaultEdge> statusChangedGraph = new SimpleWeightedGraph<>(DefaultEdge.class);
        SimpleDirectedWeightedGraph<BridgeStatus, DefaultEdge> statusChangedGraph = new SimpleDirectedWeightedGraph<>(DefaultEdge.class);

        int peopleNum = 4;
        int maxNumber = 1 << peopleNum;
        BridgeStatus[] statusArray = new BridgeStatus[1 << peopleNum];

        BridgeStatus initialStatus = new BridgeStatus(new int[]{0, 0, 0, 0}, "a0 a1 a2 a3 | ");
//        statusArray[0] = initialStatus;
        statusChangedGraph.addVertex(initialStatus);

        BridgeStatus finalStatus = new BridgeStatus(new int[]{1, 1, 1, 1}, "| a0 a1 a2 a3");
        statusChangedGraph.addVertex(finalStatus);
        // !!!!!!!!!!! 注意减号的优先级高于移位运算符
//        statusArray[(1 << peopleNum) - 1] = finalStatus;
//        statusArray[maxNumber - 1] = finalStatus;


        StringBuilder onePoint = new StringBuilder();
        StringBuilder anotherPoint = new StringBuilder();
        StringBuilder point;
        String message;

        int[] status = new int[peopleNum];
        // 生成所有可能的状态序列
        for (int i = 1; i < maxNumber - 1; i++) {
            for (int j = 0; j < peopleNum; j++) {
                status[j] = (i >> peopleNum - j - 1) & 1;
                if (status[j] == 0) {
                    point = onePoint;
                } else {
                    point = anotherPoint;
                }
                point.append("a");
                point.append(j);
                point.append(" ");
            }
            onePoint.append(" | ");
            onePoint.append(anotherPoint);
            message = onePoint.toString();
            onePoint = new StringBuilder();
            anotherPoint = new StringBuilder();

            statusArray[i] = new BridgeStatus(status, message);
            statusChangedGraph.addVertex(statusArray[i]);
        }


        Map<Integer, BridgeStatus> layer = new HashMap<>();
        layer.put(0, initialStatus);
        boolean isPassing = true;
        Queue<BridgeStatus> suitableVertex = new LinkedList<>();
        suitableVertex.offer(initialStatus);
        BridgeStatus currentVertex;
        ArrayList<Integer> statusIndexes;
        int beta = 0;
        int count = 1;
        while (!suitableVertex.isEmpty()) {
            currentVertex = suitableVertex.poll();
            if (isPassing) {
                statusIndexes = allSuitablePassing(currentVertex, statusChangedGraph.vertexSet());
            }else {
                statusIndexes = allSuitableBacking(currentVertex);
            }

            beta += addAllEdgeAndWeight(statusChangedGraph, currentVertex, layer, statusIndexes, suitableVertex, peopleNum);
            count --;
            if(count == 0){
                isPassing = !isPassing;
                count = beta;
                beta = 0;
                // 图构建结束
                if(count == 0 && layer.containsKey(maxNumber - 1)){
                    finalStatus = layer.get(maxNumber - 1);
                }
                layer.clear();
            }
        }

        DijkstraShortestPath<BridgeStatus, DefaultEdge> dijkstraShortestPath
                = new DijkstraShortestPath<>(statusChangedGraph);
        List<BridgeStatus> shortestPath = dijkstraShortestPath
                .getPath(initialStatus, finalStatus).getVertexList();
        for (BridgeStatus vertex : shortestPath) {
            System.out.println(vertex.getMessage());
            System.out.println();
        }
    }

    private int addAllEdgeAndWeight(SimpleDirectedWeightedGraph<BridgeStatus, DefaultEdge> statusChangedGraph,
                                     BridgeStatus source, Map<Integer, BridgeStatus> layer, ArrayList<Integer> statusIndexes, Queue<BridgeStatus> suitableVertex, int peopleNum){
        DefaultEdge ijEdge;
        int edgeWeight;
        int enqueueNum = 0;
        BridgeStatus currentStatus;
        int maxNum = (1 << peopleNum) - 1;
        for (int statusIndex : statusIndexes){
            if(layer.containsKey(statusIndex)){
                currentStatus = layer.get(statusIndex);
            }else{
                currentStatus = newBridgeStatusInstance(peopleNum, statusIndex);
                layer.put(statusIndex, currentStatus);
                statusChangedGraph.addVertex(currentStatus);
                if(statusIndex != maxNum){
                    suitableVertex.offer(currentStatus);
                    enqueueNum ++;
                }
            }

            ijEdge = statusChangedGraph.addEdge(source, currentStatus);
            if (ijEdge != null) {

                edgeWeight = countTime(source, currentStatus);
                statusChangedGraph.setEdgeWeight(ijEdge, edgeWeight);
            }
        }
        return enqueueNum;
    }
    private boolean isSuitablePassing(BridgeStatus a, BridgeStatus b) {
        int total = 0;
        for (int i = 0; i < a.getPeoples().length; i++) {
            total += Math.abs(a.getPeoples()[i].getStatus() - b.getPeoples()[i].getStatus());
        }

        return total == 2;
    }

    private ArrayList<Integer> allSuitablePassing(BridgeStatus a, Set<BridgeStatus> allVertex) {
        int count;
        int bitDiff;
        ArrayList<Integer> result = new ArrayList<>();
        for (BridgeStatus vertex : allVertex){
            count = 0;
            for (int i = 0; i < vertex.getPeoples().length; i++) {
                bitDiff = a.getPeoples()[i].getStatus() - vertex.getPeoples()[i].getStatus();
                if (bitDiff < 0) {
                    count++;
                } else if (bitDiff > 0) {
                    count = 0;
                    break;
                }
            }
            if (count == 2) {
                result.add(changeArrayToInt(vertex));
            }
        }
        return result;
    }

    private ArrayList<Integer> allSuitableBacking(BridgeStatus a) {
        ArrayList<Integer> result = new ArrayList<>();
        BridgeStatus tempStatus;
        People[] peoples = a.getPeoples();
        int[] tempArray = new int[peoples.length];
        for (int i = 0; i < peoples.length; i++) {
            if (peoples[i].getStatus() == 1) {
                                                     for (int j = 0; j < peoples.length; j++) {
                    if (j == i) {
                        tempArray[j] = 0;
                    } else {
                        tempArray[j] = peoples[j].getStatus();
                    }

                }
                tempStatus = new BridgeStatus(tempArray, "");
                result.add(changeArrayToInt(tempStatus));
            }
        }
        return result;
    }

    private int changeArrayToInt(BridgeStatus a) {
        int temp;
        int result = 0;
        People[] peoples = a.getPeoples();
        for (int i = 0; i < peoples.length; i++) {
            temp = peoples[i].getStatus() << peoples.length - i - 1;
            result += temp;
        }
        return result;
    }

    private int countTime(BridgeStatus a, BridgeStatus b) {
        int diff;
        int maxTimeIndex = 0;
        for (int i = 0; i < a.getPeoples().length; i++) {
            diff = Math.abs(a.getPeoples()[i].getStatus() - b.getPeoples()[i].getStatus());
            if (diff == 1) {
                maxTimeIndex = i;
            }
        }

        return a.getPeoples()[maxTimeIndex].getTime();
    }


    private BridgeStatus newBridgeStatusInstance(int peopleNum, int code){
        StringBuilder onePoint = new StringBuilder();
        StringBuilder anotherPoint = new StringBuilder();
        StringBuilder point;
        String message;
        int[] status = new int[peopleNum];
        for (int j = 0; j < peopleNum; j++) {
            status[j] = (code >> peopleNum - j - 1) & 1;
            if (status[j] == 0) {
                point = onePoint;
            } else {
                point = anotherPoint;
            }
            point.append("a");
            point.append(j);
            point.append(" ");
        }
        onePoint.append(" | ");
        onePoint.append(anotherPoint);
        message = onePoint.toString();
        return new BridgeStatus(status, message);
    }
}
