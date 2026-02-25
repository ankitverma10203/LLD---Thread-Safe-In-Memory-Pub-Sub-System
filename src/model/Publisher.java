package model;

public class Publisher<T> {
    private final String pubId;
    private final Topic<T> topic;


    public Publisher(String pubId, Topic<T> topic) {
        this.pubId = pubId;
        this.topic = topic;
    }

    public boolean publish(Message<T> message) throws InterruptedException {
        return topic.addMessage(message);
    }
}
