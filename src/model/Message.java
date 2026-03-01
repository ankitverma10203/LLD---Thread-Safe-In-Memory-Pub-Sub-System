import java.util.UUID;

public class Message<T> {
  private final String messageId;
  private final T content;
  private final String source;
  private final String destination;

  public Message(T content, String source, String destination) {
    this.messageId = UUID.randomUUID().toString();
    this.content = content;
    this.source = source;
    this.destination = destination;
  }

  public String getMessageId() {
    return messageId;
  }

  public T getContent() {
    return content;
  }

  public String getSource() {
    return source;
  }

  public String getDestination() {
    return destination;
  }
}
