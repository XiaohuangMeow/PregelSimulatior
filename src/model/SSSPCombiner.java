package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SSSPCombiner extends Combiner<SSSPMessage> {

    @Override
    public List<SSSPMessage> combine(List<SSSPMessage> list) {
        Map<Integer,SSSPMessage> map=new HashMap<>();
        for (int i=0;i<list.size();i++){
            SSSPMessage ssspMessage=list.get(i);
            if (!map.containsKey(ssspMessage.receive_id)){
                map.put(ssspMessage.receive_id,ssspMessage);
            }
            else {
                SSSPMessage ssspMessage1=map.get(ssspMessage.receive_id);
                SSSPMessage ssspMessage2=ssspMessage;
                if (ssspMessage1.message<ssspMessage2.message){
                    map.put(ssspMessage.receive_id,ssspMessage1);
                }
                else {
                    map.put(ssspMessage.receive_id,ssspMessage2);
                }
            }
        }
        list=new ArrayList<>(map.values());
        return list;
    }
}
