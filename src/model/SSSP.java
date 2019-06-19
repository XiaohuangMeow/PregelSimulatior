package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SSSP {

    public void SSSP_algorithm(Master master,int v) throws IOException {
        master.v=v;
        System.out.println("SSSP:Begin");
        SSSPVertex(master);
        master.SuperStep=0;
        master.func=1;
        List<Worker> workers =master.workers;
        while (!master.isWorkersInactive()) {
            System.out.println(++master.SuperStep+" Super Step");
            master.updateMessage();
            System.out.println("vertex computing");
            master.run();
            System.out.println("communication");
            SwapInformationSSSP(master);
            System.out.println("synchronizing");
        }
        System.out.println("SSSP:End");
        List<String> result=new ArrayList<>();
//        int cnt=0;
        for (int i=0;i<master.workersNum;i++){
            Worker worker=workers.get(i);
            for (int j=0;j<worker.vertices.size();j++){
//                System.out.println(cnt++);
                SSSPVertex vertex= (SSSPVertex) worker.vertices.get(j);
                if (vertex.vertexValue==Integer.MAX_VALUE){
                    result.add(new String(vertex.id+"\t"+"No path"));
                }
                else {
                    SSSPVertex vv=vertex;
                    StringBuilder s=new StringBuilder(vertex.id+"\t"+vertex.vertexValue+"\t");
                    List<String> path=new ArrayList<>();
                    path.add(vertex.id+"");
                    while (vv.from!=-1){
//                        System.out.println("id="+vv.id);
//                        System.out.println(vv.from);
                        path.add(0,vv.from+"");
                        for (int w=0;w<workers.size();w++){
                            if (workers.get(w).v_id_pos_map.keySet().contains(vv.from)){
//                                System.out.println("aaa");
                                int pos=workers.get(w).lookupIndex(vv.from);
                                vv=(SSSPVertex)workers.get(w).vertices.get(pos);
                                break;
                            }
                        }
                    }
                    for (int m=0;m<path.size();m++){
                        if (m==0){
                            s.append(path.get(m));
                        }
                        else {
                            s.append("->"+path.get(m));
                        }
                    }
                    result.add(s.toString());
//                    result.add(new String(vertex.id+"\t"+vertex.vertexValue));
                }
            }
        }
        System.out.println("Store the Result");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("Result/SSSP.txt"), "utf-8"));
        for (String s:result){
            writer.write(s);
            writer.newLine();
        }
        writer.close();
    }

    private void SSSPVertex(Master master){
        List<Worker> workers =master.workers;
        for (int i=0;i<master.workersNum;i++){
            Worker worker=workers.get(i);
            Map<Integer,List<Edge>> map=worker.StoreVertex;
            List<SSSPVertex> SSSPVertices=new ArrayList<>();
            int cnt=0;
            for (Integer v:map.keySet()){
                List<Edge> outedges=map.get(v);
                SSSPVertex ssspVertex=new SSSPVertex(v,worker,outedges);
                SSSPVertices.add(ssspVertex);
                worker.v_id_pos_map.put(v,cnt);
                cnt++;
            }
            worker.vertices=SSSPVertices;
        }
    }

    /**
     * workers communication for PageRank
     * send message to received list(current information queue)
     */
    void SwapInformationSSSP(Master master){
        List<Worker> workers =master.workers;
        for (int i=0;i<master.workers.size();i++){
            Worker worker=workers.get(i);
            List<SSSPMessage> list=worker.sendMessage;
            for (int j=0;j<list.size();j++){
                SSSPMessage ssspMessage=list.get(j);
                for (int k=0;k<master.workers.size();k++){
                    if (workers.get(k).v_id_pos_map.containsKey(ssspMessage.receive_id)){
                        int pos= (int) workers.get(k).lookupIndex(ssspMessage.receive_id);
                        Vertex v= (Vertex) workers.get(k).vertices.get(pos);
                        v.received.add(ssspMessage);
                        v.active=true;
                        workers.get(k).active=true;
                        break;
                    }
                }
            }
        }
    }
}
