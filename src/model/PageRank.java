package model;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PageRank {


    /**
     * PageRank Algorithm
     * the result stores in Result/PageRank.txt
     * @throws IOException
     */
    public void PageRank_algorithm(Master master) throws IOException {
        List<Worker> workers =master.workers;
        System.out.println("PageRank:Begin");
        PageRankVertex(master);
        master.SuperStep=0;
        master.func=0;
//        double temp=master.pageRankAggregator.aggregate(master.PageRankAggregatorValueList;
//        System.out.println(temp);
        while (master.pageRankAggregator.aggregate(master.PageRankAggregatorValueList)>0.0005&&master.SuperStep<100) {
            System.out.println(master.pageRankAggregator.aggregate(master.PageRankAggregatorValueList));
            master.PageRankAggregatorValueList.clear();
            System.out.println(++master.SuperStep+" Super Step");
            master.updateMessage();
            System.out.println("vertex computing");
            master.run();
            System.out.println("communication");
            SwapInformationPageRank(master);
            System.out.println("synchronizing");
        }
        System.out.println("PageRank:End");
        List<String> result=new ArrayList<>();
        for (int i=0;i<master.workersNum;i++){
            Worker worker=workers.get(i);
            for (int j=0;j<worker.vertices.size();j++){
                Vertex vertex= (Vertex) worker.vertices.get(j);
                result.add(new String(vertex.id+"\t"+vertex.vertexValue));
            }
        }
        System.out.println("Store the Result");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("Result/PageRank.txt"), "utf-8"));
        for (String s:result){
            writer.write(s);
            writer.newLine();
        }
        writer.close();
    }

    private void PageRankVertex(Master master){
        List<Worker> workers =master.workers;
        for (int i=0;i<master.workersNum;i++){
            Worker worker=workers.get(i);
            Map<Integer,List<Edge>> map=worker.StoreVertex;
            List<PageRankVertex> pageRankVertices=new ArrayList<>();
            int cnt=0;
            for (Integer v:map.keySet()){
                List<Edge> outedges=map.get(v);
                PageRankVertex pageRankVertex=new PageRankVertex(v,worker,outedges);
                pageRankVertices.add(pageRankVertex);
                worker.v_id_pos_map.put(v,cnt);
                cnt++;
            }
            worker.vertices=pageRankVertices;
        }
    }

    /**
     * workers communication for PageRank
     * send message to received list(current information queue)
     */
    private void SwapInformationPageRank(Master master){
        List<Worker> workers =master.workers;
        for (int i=0;i<workers.size();i++){
            Worker worker=workers.get(i);
            List<PageRankMessage> list=worker.sendMessage;
            for (int j=0;j<list.size();j++){
                PageRankMessage pageRankMessage=list.get(j);
                for (int k=0;k<workers.size();k++){
                    if (workers.get(k).v_id_pos_map.containsKey(pageRankMessage.receive_id)){
                        int pos= (int) workers.get(k).lookupIndex(pageRankMessage.receive_id);
                        Vertex v= (Vertex) workers.get(k).vertices.get(pos);
                        v.received.add(pageRankMessage);
                        break;
                    }
                }
            }
        }
    }
}
