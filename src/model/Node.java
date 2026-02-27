package model;

public class Node<T> {
    private final Message<T> message;
    private Node<T> next;
    private Node<T> prev;

    public Node(Message<T> message) {
        this.message = message;
        this.next = null;
    }

    public Message<T> getMessage() {
        return message;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }
}
