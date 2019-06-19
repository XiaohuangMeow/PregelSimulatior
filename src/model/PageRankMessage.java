package model;

public class PageRankMessage {
    int send_id;
    int receive_id;
    double message;

    public PageRankMessage(int send_id, int receive_id, double message) {
        this.send_id = send_id;
        this.receive_id = receive_id;
        this.message = message;
    }

    public static PageRankMessage add(PageRankMessage p1,PageRankMessage p2){
        return new PageRankMessage(p2.send_id,p2.receive_id,p2.message+p1.message);
    }

    @Override
    public String toString() {
        return "PageRankMessage{" +
                "send_id=" + send_id +
                ", receive_id=" + receive_id +
                ", message=" + message +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PageRankMessage that = (PageRankMessage) o;

        if (send_id != that.send_id) return false;
        if (receive_id != that.receive_id) return false;
        return Double.compare(that.message, message) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = send_id;
        result = 31 * result + receive_id;
        temp = Double.doubleToLongBits(message);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
