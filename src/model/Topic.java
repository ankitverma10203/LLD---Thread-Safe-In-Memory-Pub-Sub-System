package model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Topic<T> {
  private final String name;
  private final Set<Subscriber<T>> subscribers;
  private final List<Message<T>> messages;
  private final int capacity;
  private final AtomicInteger startOffset;

  public Topic(String name, int capacity) {
    this.name = name;
    this.capacity = capacity;
    this.subscribers = ConcurrentHashMap.newKeySet();
    this.messages = new ArrayList<>();
    this.startOffset = new AtomicInteger(0);
  }

  public boolean addSubscriber(Subscriber<T> subscriber) {
    return subscribers.add(subscriber);
  }

  public String getName() {
    return name;
  }

  public int getStartOffset() {
    return startOffset.get();
  }

  public synchronized boolean addMessage(Message<T> message) {
    if (messages.size() == capacity) {
      messages.remove(0);
      startOffset.incrementAndGet();
    }

    messages.add(message);
    return true;
  }

  public synchronized Message<T> getMessageByOffset(int offset) throws Exception {
    int currentStart = startOffset.get();
    int index = offset - currentStart;

    if (index < 0 || index >= messages.size()) {
      throw new Exception("Message not present in the topic");
    }

    return messages.get(index);
  }
}
