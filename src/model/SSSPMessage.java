package model;

public class SSSPMessage {
    protected int send_id;
    protected int receive_id;
    protected int message;

    public SSSPMessage(int send_id, int receive_id, int message) {
        this.send_id = send_id;
        this.receive_id = receive_id;
        this.message = message;
    }

    @Override
    public String toString() {
        return "SSSPMessage{" +
                "send_id=" + send_id +
                ", receive_id=" + receive_id +
                ", message=" + message +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SSSPMessage that = (SSSPMessage) o;

        if (send_id != that.send_id) return false;
        if (receive_id != that.receive_id) return false;
        return message == that.message;
    }

    @Override
    public int hashCode() {
        int result = send_id;
        result = 31 * result + receive_id;
        result = 31 * result + message;
        return result;
    }
}
