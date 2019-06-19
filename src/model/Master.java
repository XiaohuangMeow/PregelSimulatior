package model;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class Master<VertexValue, EdgeValue, MessageValue> implements Runnable {

    protected int SuperStep = 0;
    protected List<Worker> workers = new ArrayList<>();
    protected int workersNum;
    protected CountDownLatch latch = new CountDownLatch(workersNum);
    public int vertexNum;
    protected int v;
    protected int func=-1;
    protected boolean combiner=false;
    protected boolean aggregator=false;
    protected PageRankAggregator pageRankAggregator=new PageRankAggregator(this);
    protected List<Double> PageRankAggregatorValueList=new ArrayList<>();
    protected Statisticians statisticians=new Statisticians(workersNum);

    public Master(int workersNum) {
        this.workersNum = workersNum;
        for (int i = 0; i < workersNum; i++) {
            workers.add(new Worker(i,this));
        }
    }

    public void statistics(){
        for (int i=0;i<workersNum;i++){
            int vertex_num=workers.get(i).vertices.size();
            int edge_num=0;
            for (int j=0;j<workers.get(i).vertices.size();j++){
                Vertex v=(Vertex)workers.get(i).vertices.get(j);
                edge_num+=v.outEdges.size();
            }
        }
    }

    public void SSSP(int v,int u) throws IOException {
        new SSSP().SSSP_algorithm(this,v);
    }

    /**
     * PageRank Algorithm
     * the result stores in Result/PageRank.txt
     * @throws IOException
     */
    public void PageRank() throws IOException {
        new PageRank().PageRank_algorithm(this);
        System.out.println(statisticians);
    }

    /**
     * clear the old information queue
     * get ready to use the new information queue to compute
     */
    void updateMessage(){
        for (int i=0;i<workers.size();i++){
            Worker worker=workers.get(i);
            List<? extends Vertex> list=worker.vertices;
            for (int j=0;j<list.size();j++){
                Vertex v=list.get(j);
                v.lastReceived.clear();
                v.lastReceived.addAll(v.received);
                v.received.clear();
//                System.out.println(v.lastReceived);
            }
        }
    }

    protected boolean isWorkersInactive() {
        for (Worker worker : workers) {
            if (worker.active) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void run() {
        for (Worker worker : workers) {
            new Thread(worker).run();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Map<Integer,List<Edge>> readGraph() throws IOException {
//        List<String> lines= Files.readAllLines(Paths.get("test.txt"), Charset.forName("UTF-8"));
        List<String> lines= Files.readAllLines(Paths.get("web-Google.txt"), Charset.forName("UTF-8"));
        Map<Integer,List<Edge>> map=new HashMap<>();
        for (int i=0;i<lines.size();i++){
            String line=lines.get(i);
            if (line.charAt(0)=='#'){
                continue;
            }
            String[] nodes=line.split("\\t");
            int source=Integer.valueOf(nodes[0]);
            int destination=Integer.valueOf(nodes[1]);
            Edge edge=new Edge(source,destination);
            if (map.containsKey(source)){
                List<Edge> outEdges=map.get(source);
                outEdges.add(edge);
                map.put(source,outEdges);
            }
            else{
                List<Edge> newlist=new ArrayList<>();
                newlist.add(edge);
                map.put(source,newlist);
            }
        }
        return map;
    }

    /**
     * Edge-cut
     * use the round-robin algorithm to implement
     * @throws IOException
     */
    public void Partition() throws IOException {
        Map<Integer,List<Edge>> map=readGraph();
        List<Edge>[] save=new List[workersNum];
        for (int i=0;i<workersNum;i++){
            save[i]=new ArrayList<>();
        }
        int cnt=0;
        for (Map.Entry<Integer,List<Edge>> entry:map.entrySet()){
            int vertexID=entry.getKey();
            List<Edge> lst=entry.getValue();
            for (Edge edge:lst){
                save[cnt%workersNum].add(edge);
            }
            cnt++;
        }
        for (int i=0;i<workersNum;i++){
            List<Edge> edges=save[i];
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("Partition/save"+i+".txt"), "utf-8"));
            for (Edge edge:edges){
                String s=edge.getFrom()+"\t"+edge.getTo();
                writer.write(s);
                writer.newLine();
            }
            writer.close();
        }
    }

    /**
     * read the graph of partition result
     * @throws IOException
     */
    public void Load() throws IOException {
        Set<Integer> set=new HashSet<>();
        Set<Integer> in=new HashSet<>();
        for (int i=0;i<workersNum;i++) {
            Map<Integer,List<Edge>> map=new HashMap<>();
            List<String> lines = Files.readAllLines(Paths.get("Partition/save"+i+".txt"), Charset.forName("UTF-8"));
            for (String line:lines){
                String[] nodes=line.split("\\t");
                int source=Integer.valueOf(nodes[0]);
                int destination=Integer.valueOf(nodes[1]);
                set.add(source);
                set.add(destination);
                in.add(source);
                Edge edge=new Edge(source,destination);
                if (map.containsKey(source)){
                    List<Edge> outEdges=map.get(source);
                    outEdges.add(edge);
                    map.put(source,outEdges);
                }
                else{
                    List<Edge> newlist=new ArrayList<>();
                    newlist.add(edge);
                    map.put(source,newlist);
                }
            }
            workers.get(i).StoreVertex=map;
            workers.get(i).vertex_num=map.keySet().size();
            workers.get(i).edge_num=lines.size();
        }
        int w=0;
        for (Integer v:set){
            if (!in.contains(v)){
                List<Edge> temp=new ArrayList<>();
                workers.get(w).StoreVertex.put(v,temp);
                w=(w+1)%workersNum;
            }
        }
        vertexNum=set.size();
    }

    public static void main(String[] args) throws IOException {
        int workersNum=10;
        Master master=new Master(workersNum);
        master.combiner=true;
//        master.Partition();
        master.Load();
//        master.PageRank();
        master.SSSP(0,11342);
    }
}
