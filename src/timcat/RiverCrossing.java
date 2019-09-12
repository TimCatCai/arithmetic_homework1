package timcat;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import java.util.*;

public class RiverCrossing {
    public void crossing(){
        // 状态转换图
        SimpleGraph<RiverStatus, DefaultEdge> statusChangeGraph = new SimpleGraph<>(DefaultEdge.class);
        // 状态一 0000
        RiverStatus initialRiverStatus = new RiverStatus(0,0,0,0, "初始状态");
        statusChangeGraph.addVertex(initialRiverStatus);
        // 状态二 0100
        statusChangeGraph.addVertex(new RiverStatus(0, 1, 0, 0, "农夫羊菜 | 狼"));
        // 状态三 0010
        statusChangeGraph.addVertex(new RiverStatus(0, 0, 1, 0, "农夫狼菜 | 羊"));
        // 状态四 0001
        statusChangeGraph.addVertex(new RiverStatus(0, 0, 0, 1, "农夫狼羊 | 菜"));
        // 状态五 1010
        statusChangeGraph.addVertex(new RiverStatus(1, 0, 1, 0, "狼菜 | 农夫羊"));
        // 状态六 0101
        statusChangeGraph.addVertex(new RiverStatus(0, 1, 0, 1, "农夫羊 | 狼菜"));
        // 状态七 1011
        statusChangeGraph.addVertex(new RiverStatus(1, 0, 1, 1, "狼 | 农夫羊菜"));
        // 状态八 1101
        statusChangeGraph.addVertex(new RiverStatus(1, 1, 0, 1, "羊 | 农夫狼菜"));
        // 状态九 1110
        statusChangeGraph.addVertex(new RiverStatus(1, 1, 1, 0, "菜 | 农夫狼羊"));
        // 状态十 1111
        RiverStatus finalRiverStatus = new RiverStatus(1, 1, 1, 1, "已经全部过河");
        statusChangeGraph.addVertex(finalRiverStatus);


        // 添加状态转换的边
        for(RiverStatus i: statusChangeGraph.vertexSet()){
            for(RiverStatus j: statusChangeGraph.vertexSet()){
                // 这里没有必要判断是否是同一状态，因为在判断农民是否有往返时，即可筛选掉同一状态的情况
                // 同时后面的isOneChanged也可以判断是否是同一状态
                if(
                    // 判断农民是否有往返
                    i.getFarmer() != j.getFarmer()
                    // 判断羊、狼、白菜是否只有一个过河
                    && isOneChanged(i, j)){
                    // 两种状态之间可以相互转化，在图顶点之间构建边
                    statusChangeGraph.addEdge(i, j);
                }
            }
        }
        Iterator<RiverStatus> it = new DepthFirstIterator<>(statusChangeGraph);
        RiverStatus riverStatus;
        while(it.hasNext()){
            riverStatus = it.next();
            System.out.println(riverStatus.getMessage());
            if(riverStatus == finalRiverStatus){
                break;
            }
        }
        Stack<RiverStatus> temp = new Stack<>();
        searchForResult(statusChangeGraph, initialRiverStatus, finalRiverStatus, temp);

    }

    private boolean isOneChanged(RiverStatus a, RiverStatus b){
        int wolfDiff = Math.abs(a.getWolf() - b.getWolf());
        int cabbageDiff = Math.abs(a.getCabbage() - b.getCabbage());
        int sheepDiff = Math.abs(a.getSheep() - b.getSheep());
        // 这里可以判断是否是同一个状态，即当和为0时即可判断a,b为同一状态
        return wolfDiff + cabbageDiff + sheepDiff <= 1;
    }

    private void searchForResult(SimpleGraph<RiverStatus, DefaultEdge> graph, RiverStatus startVertex, RiverStatus endVertex, Stack<RiverStatus> result){
        if(startVertex == endVertex){
            for(RiverStatus riverStatus : result){
                System.out.println(riverStatus.getMessage());
            }
            System.out.println(endVertex.getMessage());
            System.out.println();
            return;
        }

        result.push(startVertex);

        Set<DefaultEdge> edgeSet = graph.edgesOf(startVertex);
        Iterator<DefaultEdge> it = edgeSet.iterator();
        DefaultEdge temp;
        RiverStatus nextVertex;
        // 深度优先历遍
        while(it.hasNext()){
            temp = it.next();
            // 获取该边下一个结点
            nextVertex = graph.getEdgeTarget(temp);
            // 如果该节点没有访问过
            if(!nextVertex.isVisited()){
                // 将结点设置为访问过的结点
                nextVertex.setVisited(true);
                searchForResult(graph, nextVertex, endVertex, result);
                // 回溯时将该节点设置为未历遍
                nextVertex.setVisited(false);
            }
        }
    }
}
