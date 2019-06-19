package model;

import java.util.List;

public class PageRankVertex extends Vertex<Double,Integer,PageRankMessage> {

//    double d=0.85;
    double d=0.85;

    public PageRankVertex(int id, Worker worker, List<Edge> outEdges) {
        super(id, worker, outEdges);
        vertexValue=1.0/worker.master.vertexNum;
//        System.out.println(vertexValue);
//        vertexValue=1.0;
    }

    @Override
    public void SendMessageTo(PageRankMessage message) {
        this.worker.sendMessage.add(message);
    }

    @Override
    public void compute() {
        double old=vertexValue;
        double rank=0;
        for (int i=0;i<lastReceived.size();i++){
            rank+=lastReceived.get(i).message;
        }
        if (worker.master.SuperStep==1){
            rank=1.0/worker.master.vertexNum;
            vertexValue=rank;
//            vertexValue=1.0;
        }
        else {
//            vertexValue=d*rank+(1-d);
            vertexValue=d*rank+(1-d)/worker.master.vertexNum;
//            if (id==525166){
//                System.out.println(rank);
//                System.out.println(vertexValue);
//            }
        }
//        System.out.println(this.worker.master.PageRankAggregatorValueList.size());
        this.worker.master.pageRankAggregator.report(Math.abs(old-vertexValue));
//        System.out.println(this.worker.master.PageRankAggregatorValueList.size());
        int edgeNum=outEdges.size();
        double value=vertexValue/edgeNum;
        for (int i=0;i<outEdges.size();i++){
            Edge edge=outEdges.get(i);
            int from=edge.getFrom();
            int to=edge.getTo();
            PageRankMessage pageRankMessage=new PageRankMessage(from,to,value);
            SendMessageTo(pageRankMessage);
        }
    }

}
