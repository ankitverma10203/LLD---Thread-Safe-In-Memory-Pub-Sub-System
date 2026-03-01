package model;

import java.util.concurrent.atomic.AtomicInteger;

public class Subscriber<T> {
  private final String subId;
  private final Topic<T> topic;
  private final AtomicInteger readOffset;

  public Subscriber(String subId, Topic<T> topic) {
    this.subId = subId;
    this.topic = topic;
    this.readOffset = new AtomicInteger(topic.getStartOffset());
  }

  public String getSubId() {
    return subId;
  }

  public Message<T> poll() throws Exception {
    int currentOffset = readOffset.get();
    Message<T> message = topic.getMessageByOffset(currentOffset);
    readOffset.incrementAndGet();
    return message;
  }
}
