package model;

import java.util.List;

public class SSSPVertex extends Vertex<Integer,Integer,SSSPMessage>{

    protected int from=-1;

    public SSSPVertex(int id, Worker worker, List<Edge> outEdges) {
        super(id, worker, outEdges);
        vertexValue=Integer.MAX_VALUE;
        if (id==worker.master.v){
            vertexValue=0;
        }
    }

    @Override
    public void SendMessageTo(SSSPMessage message) {
        this.worker.sendMessage.add(message);
    }

    @Override
    public void compute() {
        if (!active){
            return;
        }
        int temp_from = -1;
        int min=Integer.MAX_VALUE;
        if (this.worker.master.SuperStep==1&&id==this.worker.master.v){
            for (int i=0;i<outEdges.size();i++){
                Edge edge=outEdges.get(i);
                int from=edge.getFrom();
                int to=edge.getTo();
                SendMessageTo(new SSSPMessage(from,to,1));
            }
            VoteToHalt();
            return;
        }
        for (int i=0;i<lastReceived.size();i++){
            if (lastReceived.get(i).message<min){
                min=lastReceived.get(i).message;
                temp_from=lastReceived.get(i).send_id;
            }
        }
        if (min<getValue()){
            from=temp_from;
            ModifyVertexValue(min);
            for (int i=0;i<outEdges.size();i++){
                Edge edge=outEdges.get(i);
                int from=edge.getFrom();
                int to=edge.getTo();
                SendMessageTo(new SSSPMessage(from,to,min+1));
            }
        }
        VoteToHalt();
    }
}
