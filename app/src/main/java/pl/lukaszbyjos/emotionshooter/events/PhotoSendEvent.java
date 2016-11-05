package pl.lukaszbyjos.emotionshooter.events;

public class PhotoSendEvent {
    private boolean send;
    private String reason;

    public PhotoSendEvent() {

    }

    public PhotoSendEvent(boolean send, String reason) {
        this.send = send;
        this.reason = reason;
    }

    public PhotoSendEvent(boolean send) {

        this.send = send;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    @Override
    public String toString() {
        return "PhotoSendEvent{" +
                "send=" + send +
                ", reason='" + reason + '\'' +
                '}';
    }
}
