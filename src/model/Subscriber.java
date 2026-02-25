package model;

import java.util.concurrent.atomic.AtomicInteger;

public class Subscriber<T> {
    private final String subId;
    private final Topic<T> topic;
    private final AtomicInteger readOffset;

    public Subscriber(String subId, Topic<T> topic) {
        this.subId = subId;
        this.topic = topic;
        this.readOffset = new AtomicInteger(0);
    }

    public Message<T> getMessage() throws Exception {
        return topic.getMessageByOffset(readOffset.getAndIncrement());
    }
}
