package model;

import java.util.List;

public abstract class Combiner<MessageValue> {

    abstract public List<MessageValue> combine(List<MessageValue> list);
}
