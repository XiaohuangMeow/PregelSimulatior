package model;

import java.util.List;

public abstract class Aggregator<Value> {

    Master master;

    public Aggregator(Master master) {
        this.master=master;
    }

    abstract public void report(Value messageValue);
    abstract public Value aggregate(List<Value> list);
}
