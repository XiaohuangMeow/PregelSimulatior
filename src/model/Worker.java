package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Worker <VertexValue,EdgeValue,MessageValue> implements  Runnable {

    protected int id;
    protected boolean active=true;
    protected Master master;
    protected Map<Integer,List<Edge>> StoreVertex;
    protected List<? extends Vertex> vertices;
    protected List<MessageValue> sendMessage=new ArrayList<>();
    protected Map<Integer,Integer> v_id_pos_map=new HashMap<>();
    protected int vertex_num;
    protected int edge_num;

    public <VertexValue, EdgeValue, MessageValue> Worker(int id,Master master) {
        this.id=id;
        this.master=master;
    }

    private boolean isVertexInactive() {
        for (Vertex vertex : vertices) {
            if (vertex.active) {
                return false;
            }
        }
        return true;
    }

    protected int lookupIndex(int id){
        return v_id_pos_map.get(id);
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        sendMessage.clear();
        if (active){
            for (Vertex vertex:vertices){
                vertex.compute();
            }
            if (isVertexInactive()){
                this.active=false;
            }
        }
        if (master.func==0&&master.combiner){
            sendMessage= (List<MessageValue>) new PageRankCombiner().combine((List<PageRankMessage>) sendMessage);
        }
        else if (master.func==1&&master.combiner){
            sendMessage= (List<MessageValue>) new SSSPCombiner().combine((List<SSSPMessage>) sendMessage);
        }
        long endTime = System.currentTimeMillis();
        this.master.statisticians.addInfo(master.SuperStep,this.id,endTime-startTime,sendMessage.size());
        master.latch.countDown();

    }

}
