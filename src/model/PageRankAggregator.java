package model;

import java.util.List;

public class PageRankAggregator extends Aggregator<Double>{


    public PageRankAggregator(Master master) {
        super(master);
    }

    @Override
    public void report(Double aDouble) {
        this.master.PageRankAggregatorValueList.add(aDouble);
    }

    @Override
    public Double aggregate(List<Double> list) {
        if (master.SuperStep<5){
            return Double.MAX_VALUE;
        }
        double sum=0;
        for (Double d:list){
            sum+=d;
        }
        return sum;
    }
}
