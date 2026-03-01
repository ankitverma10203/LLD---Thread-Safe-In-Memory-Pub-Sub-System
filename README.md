# LLD — Thread-Safe In-Memory In-Memory Pub-Sub System

A small Java implementation of a thread-safe, in-memory publish–subscribe (pub-sub) system intended for learning and low-scale experiments. The project demonstrates a minimal design with Topics, Publishers, Subscribers, and typed Messages, focusing on simple, explicit concurrency controls.

## Goals
- Provide a clear, minimal pub-sub model implemented in plain Java.
- Illustrate thread-safety primitives (synchronized, AtomicInteger, ConcurrentHashMap.newKeySet()).
- Offer an easy-to-run example for experimentation and extension.

## High-level architecture (textual)

Components and relationships:
- Publisher -> Topic <- Subscriber
- A `Publisher` publishes `Message<T>` objects to a `Topic<T>`.
- A `Subscriber` polls messages from a `Topic<T>` using an offset it maintains per-subscriber.

Textual diagram:

Publisher --(publish message)--> Topic <---(poll)--- Subscriber
                                   │
                                   └─ messages (in-memory, bounded list)

## Class-by-class overview

All code is under `src/model`.

- Message<T>
  - Purpose: Immutable container for data sent through the system.
  - Key fields: `messageId` (UUID), `content` (T), `source` (String), `destination` (String).
  - Important methods: `getMessageId()`, `getContent()`, `getSource()`, `getDestination()`.

- Publisher<T>
  - Purpose: Responsible for publishing messages to a specific `Topic<T>`.
  - Key fields: `pubId` (String) and `topic` (Topic<T>).
  - Important methods:
    - `publish(Message<T> message)` — forwards the message to `Topic.addMessage(message)` and returns the boolean result. It declares `throws InterruptedException` (keeps compatibility with potential blocking implementations).
    - `getPubId()` — accessor.

- Subscriber<T>
  - Purpose: Represents a consumer that polls messages from an assigned topic.
  - Key fields: `subId` (String), `topic` (Topic<T>), `readOffset` (AtomicInteger).
  - Behavior:
    - The constructor initializes the subscriber's `readOffset` to `topic.getStartOffset()` so new subscribers start at the current start of the topic.
    - `poll()` attempts to read the message at the subscriber's current offset via `topic.getMessageByOffset(currentOffset)`, increments the offset, and returns the message. `poll()` may throw an exception when the requested offset is no longer available.

- Topic<T>
  - Purpose: Holds messages for a logical topic and manages subscribers.
  - Key fields: `name` (String), `subscribers` (ConcurrentHashMap.newKeySet()), `messages` (List<Message<T>>), `capacity` (int), `startOffset` (AtomicInteger).
  - Important methods:
    - `addSubscriber(Subscriber<T> subscriber)` — registers a subscriber in a concurrent set.
    - `addMessage(Message<T> message)` — synchronized: appends message; when capacity is reached, removes the oldest message and increments `startOffset`.
    - `getMessageByOffset(int offset)` — synchronized: converts offset to list index (offset - startOffset) and returns the message or throws `Exception("Message not present in the topic")` if out of range.
    - `getStartOffset()` and `getName()` — accessors.

- Main
  - Minimal starter class printing sample output; not used to demonstrate the pub-sub usage in source.

## Concurrency & thread-safety notes
- Topic uses `synchronized` on `addMessage` and `getMessageByOffset` to protect the message list and eviction logic.
- `subscribers` is a concurrent set created with `ConcurrentHashMap.newKeySet()` so registrations are thread-safe.
- `startOffset` and each `Subscriber`'s `readOffset` are `AtomicInteger`s to safely manage integer updates across threads.

Limitations and caveats
- Eviction is immediate: when capacity is full, the oldest message is removed and `startOffset` increments. Subscribers that are still pointing to evicted offsets will get an exception from `getMessageByOffset`.
- `Subscriber.poll()` increments its offset even if `getMessageByOffset` throws — callers should handle exceptions and be aware of possible message loss.
- The design keeps message storage in-memory only (no persistence) and lacks back-pressure, acknowledgements, or re-delivery semantics.

## Usage example

Example (simple, synchronous):

```java
// create topic
Topic<String> topic = new Topic<>("news", 100);

// create subscriber and register it
Subscriber<String> sub = new Subscriber<>("sub1", topic);
topic.addSubscriber(sub);

// create publisher
Publisher<String> pub = new Publisher<>("pub1", topic);

// publish a message
Message<String> m = new Message<>("Hello world", pub.getPubId(), topic.getName());
try {
  pub.publish(m);
} catch (InterruptedException e) {
  Thread.currentThread().interrupt();
}

// subscriber polls
try {
  Message<String> received = sub.poll();
  System.out.println("Received: " + received.getContent());
} catch (Exception e) {
  System.err.println("Failed to poll message: " + e.getMessage());
}
```

Note: `publish` declares `throws InterruptedException`. `Subscriber.poll()` may throw `Exception` when the requested offset is unavailable.

## Build & run
Assuming JDK is installed and you are in the project root:

```bash
# compile
javac -d out src/model/*.java src/Main.java

# run (example)
java -cp out Main
```

Adjust classpath and package names if you introduce packages.

## Testing & edge cases to consider
- Subscriber polls a message after it was evicted (expect `getMessageByOffset` to throw).
- Concurrent publishers racing to append messages (verify ordering and consistency).
- Subscribers registering after messages exist: they start from `topic.getStartOffset()`.
- Publish `null` messages (current `Publisher.publish` returns `false` if `message == null`).
- Capacity boundary: ensure eviction correctly increments `startOffset` and keeps list size <= capacity.

## Roadmap / suggested improvements
- Add durable storage or snapshotting for persistence.
- Implement per-subscriber offsets stored in topic to survive restarts.
- Add acknowledgement & re-delivery semantics (at-least-once / exactly-once options).
- Replace simple synchronization with ReadWriteLock or more granular concurrency for higher throughput.
- Provide non-blocking publish (bounded queues) and back-pressure to publishers.
- Add unit tests and multi-threaded integration tests.

---

If you want, I can commit this README for you and run a quick compile of the Java sources to validate the project still builds — tell me if you'd like me to proceed with that.
