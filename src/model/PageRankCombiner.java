package model;

import model.Combiner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageRankCombiner extends Combiner<PageRankMessage> {

    @Override
    public List<PageRankMessage> combine(List<PageRankMessage> list) {
        Map<Integer,PageRankMessage> map=new HashMap<>();
        for (int i=0;i<list.size();i++){
            PageRankMessage pageRankMessage=list.get(i);
            if (!map.containsKey(pageRankMessage.receive_id)){
                map.put(pageRankMessage.receive_id,pageRankMessage);
            }
            else {
                PageRankMessage add1=map.get(pageRankMessage.receive_id);
                PageRankMessage add2=pageRankMessage;
                PageRankMessage add=PageRankMessage.add(add1,add2);
                map.put(pageRankMessage.receive_id,add);
//                System.out.println(add1+"  "+add2+"  "+add);
            }
        }
        list=new ArrayList<>(map.values());
        return list;
    }
}
