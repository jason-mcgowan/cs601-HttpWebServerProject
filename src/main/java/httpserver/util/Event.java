package httpserver.util;

import java.util.HashSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;

/**
 * An event (observer) system for sending information to observers in a decoupled way. Listeners
 * must be cautious of the state of the invoking thread and be prepared to handle interruptions.
 * <p>
 * The calling object is meant to pass a reference to itself and any argument information using the
 * invoke method.
 *
 * @param <T> The type of argument the event will pass.
 * @author Jason McGowan
 */
public class Event<T> {

  private final HashSet<BiConsumer<Object, T>> subscribers = new HashSet<>();
  private final ReentrantReadWriteLock subLock = new ReentrantReadWriteLock();

  public void subscribe(EventListener<T> subscriber) {
    subscribe(subscriber.getSubscriber());
  }

  public void subscribe(BiConsumer<Object, T> delegate) {
    subLock.writeLock().lock();
    try {
      subscribers.add(delegate);
    } finally {
      subLock.writeLock().unlock();
    }
  }

  public void unsubscribe(BiConsumer<Object, T> subscriber) {
    subLock.writeLock().lock();
    try {
      subscribers.remove(subscriber);
    } finally {
      subLock.writeLock().unlock();
    }
  }

  public void unsubscribeAll() {
    subLock.writeLock().lock();
    try {
      subscribers.clear();
    } finally {
      subLock.writeLock().unlock();
    }
  }

  /**
   * This is meant to ONLY be called by the owning object instance.
   * @param owner Typically pass the owning object instance
   * @param args The arguments associated with the event
   */
  public void invoke(Object owner, T args) {
    subLock.readLock().lock();
    try {
      subscribers.forEach(sub -> sub.accept(owner, args));
    } finally {
      subLock.readLock().unlock();
    }
  }
}
