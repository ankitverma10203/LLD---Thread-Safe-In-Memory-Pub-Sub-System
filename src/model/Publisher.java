public class Publisher<T> {
  private final String pubId;
  private final Topic<T> topic;

  public Publisher(String pubId, Topic<T> topic) {
    this.pubId = pubId;
    this.topic = topic;
  }

  public boolean publish(Message<T> message) throws InterruptedException {
    if (message == null) {
      return false;
    }
    return topic.addMessage(message);
  }

  public String getPubId() {
    return pubId;
  }
}
