package model;

import java.util.ArrayList;
import java.util.List;

class Info{
    int super_step;
    int worker_id;
    long time;
    int MessageNum;

    public Info(int super_step, int worker_id, long time, int messageNum) {
        this.super_step = super_step;
        this.worker_id = worker_id;
        this.time = time;
        MessageNum = messageNum;
    }

    @Override
    public String toString() {
        return "Info{" +
                "super_step=" + super_step +
                ", worker_id=" + worker_id +
                ", time=" + time +"ms"+
                ", MessageNum=" + MessageNum +
                '}';
    }
}

public class Statisticians {

    int workerNum;
    List<Info> list=new ArrayList<>();

    public Statisticians(int workerNum) {
        this.workerNum=workerNum;
    }

    public void addInfo( int super_step,int worker_id,long time,int MessageNum){
        list.add(new Info(super_step,worker_id,time,MessageNum));
    }

    public void getInfo(int super_step,int worker_id){
        for (Info info:list){
            if (info.super_step==super_step&&info.worker_id==worker_id){
                System.out.println(info);
                break;
            }
        }
    }

    @Override
    public String toString() {
        return "Statisticians{" +
                "list=" + list +
                '}';
    }
}
