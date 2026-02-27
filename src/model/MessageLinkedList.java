package model;

import java.util.HashMap;

public class MessageLinkedList<T> {
    private Node head;
    private Node tail;
    private final HashMap<Integer, Node> map = new HashMap<>();
    private int size;
    private int capacity;

    public MessageLinkedList(int capacity) {
        this.head = null;
        this.tail = null;
        this.capacity = capacity;
    }
}
