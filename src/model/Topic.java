package model;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Topic<T> {
    private final String name;
    private final Set<Subscriber<T>> subscribers;
    private final ArrayBlockingQueue<Message<T>> messages;
    private final AtomicInteger startOffset;

    public Topic(String name, int queueCapacity) {
        this.name = name;
        this.subscribers = ConcurrentHashMap.newKeySet();
        this.messages = new ArrayBlockingQueue<>(queueCapacity);
        this.startOffset = new AtomicInteger(0);
    }

    public boolean addSubscriber(Subscriber<T> subscriber) {
        return subscribers.add(subscriber);
    }

    public String getName() {
        return name;
    }

    public Set<Subscriber<T>> getSubscribers() {
        return subscribers;
    }

    public Queue<Message<T>> getMessages() {
        return messages;
    }

    public int getStartOffset() {
        return startOffset.get();
    }

    public void updateStartOffSet(int startOffset) {
        this.startOffset.set(startOffset);
    }

    public boolean addMessage(Message<T> message) throws InterruptedException {
        messages.put(message);
        return true;
    }

    public Message<T> getMessageByOffset(int offset) throws Exception {
        if (offset < startOffset.get() || offset > startOffset.get() + messages.size()) {
            throw new Exception("Message not present in the Queue");
        }
        return messages.stream().skip(offset).findFirst().orElse(null);
    }
}
