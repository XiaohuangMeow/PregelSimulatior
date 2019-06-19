package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//class sendMessage<MessageValue>{
//    int id;
//    MessageValue messageValue;
//
//    public sendMessage(int id, MessageValue messageValue) {
//        this.id = id;
//        this.messageValue = messageValue;
//    }
//}

public abstract class Vertex <VertexValue,EdgeValue,MessageValue>{

    protected int id;
    protected Worker worker;
    protected VertexValue vertexValue;
    protected List<Edge> outEdges;
    protected boolean active=true;
    protected List<MessageValue> lastReceived=new ArrayList<>();
    protected List<MessageValue> received=new ArrayList<>();

    public Vertex(int id,Worker worker,List<Edge> outEdges) {
        this.id = id;
        this.worker=worker;
        this.outEdges=outEdges;
    }

    public VertexValue getValue(){
        return vertexValue;
    }

    public VertexValue ModifyVertexValue(VertexValue newVertexValue){
        vertexValue=newVertexValue;
        return newVertexValue;
    }

    abstract public void SendMessageTo(MessageValue message);

    public void VoteToHalt(){
        active=false;
    }

    abstract public void compute();

}
