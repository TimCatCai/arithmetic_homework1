package timcat;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.*;

/**
 * 基本的思路是将每次往返可能的状态全部列出，之后根据每次过桥为两个人，返回为一个人的思路
 * 构建成一个有向带权图，最后使用最短路径算法，找出最短的路径，即为所要的结果。
 * @author timcatcai 蔡浩明 3117004874
 *  Github 地址：https://github.com/TimCatCai/arithmetic_homework1
 */
public class BridgeClossing {

    // 过河的主要函数
    public void crossing() {
//        SimpleWeightedGraph<BridgeStatus, DefaultEdge> statusChangedGraph = new SimpleWeightedGraph<>(DefaultEdge.class);
        SimpleDirectedWeightedGraph<BridgeStatus, DefaultEdge> statusChangedGraph = new SimpleDirectedWeightedGraph<>(DefaultEdge.class);

        // 过河的人数
        int peopleNum = 4;
        int maxNumber = 1 << peopleNum;
        BridgeStatus[] statusArray = new BridgeStatus[1 << peopleNum];

        // 将初始的状态设置为最短路径的起始结点
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
        // 使用队列构建有向带权图，图的每一层代表一次过桥或返回
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

        int totalTime = 0;
        DijkstraShortestPath<BridgeStatus, DefaultEdge> dijkstraShortestPath
                = new DijkstraShortestPath<>(statusChangedGraph);
        List<BridgeStatus> shortestPath = dijkstraShortestPath
                .getPath(initialStatus, finalStatus).getVertexList();
        BridgeStatus vertexBefore = shortestPath.get(0);
        System.out.println(vertexBefore.getMessage());
        BridgeStatus vertexAfter;
        // 寻找最短路径，并计算最短的时间
        for (int i = 1; i < shortestPath.size(); i++) {
            vertexAfter = shortestPath.get(i);
            System.out.println(vertexAfter.getMessage());
            totalTime += countTime(vertexBefore, vertexAfter);
            vertexBefore = vertexAfter;
        }

        System.out.println("TotalTime: " + totalTime);
    }

    /**
     * 添加每一层的边并设置每条边的权重
     * @param statusChangedGraph 代表历遍的顺序的图
     * @param source 有向边的起始结点
     * @param layer 每一层的结点记录
     * @param statusIndexes 所要添加结点每个人的状态值构成的数值
     * @param suitableVertex
     * @param peopleNum 人数
     * @return
     */
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

    /**
     * 找出该节点所有过桥的情况
     * @param a 起始结点
     * @param allVertex 所有状态可能值
     * @return
     */
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

    /**
     * 该节点返回的所有情况
     * @param a
     * @return
     */
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


    /**
     * 将每个结点的所有人的状态值转换成数值
     * @param a
     * @return
     */
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

    /**
     * 计算邻接顶点所有的时间，即权重
     * @param a
     * @param b
     * @return
     */
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


    /**
     * 创建新的状态实例
     * @param peopleNum
     * @param code
     * @return
     */
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
