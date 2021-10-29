package httpserver.util;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;

public class Event<A extends EventArg> {

  private final HashSet<BiConsumer<Object, A>> subscribers = new HashSet<>();
  private final ReentrantReadWriteLock subLock = new ReentrantReadWriteLock();
  private final Object poolLock = new Object();
  private volatile ExecutorService threadPool;

  public void subscribe(BiConsumer<Object, A> subscriber) {
    subLock.writeLock().lock();
    try {
      subscribers.add(subscriber);
    } finally {
      subLock.writeLock().unlock();
    }
  }

  public void unsubscribe(BiConsumer<Object, A> subscriber) {
    subLock.writeLock().lock();
    try {
      subscribers.remove(subscriber);
    } finally {
      subLock.writeLock().unlock();
    }
  }

  public void invoke(Object owner, A args) {
    subLock.readLock().lock();
    try {
      subscribers.forEach(sub -> sub.accept(owner, args));
    } finally {
      subLock.readLock().unlock();
    }
  }

  public void invokeAsync(Object owner, A args) {
    setupAsyncOperation();
    subLock.readLock().lock();
    try {
      subscribers.forEach(sub -> threadPool.execute(() -> sub.accept(owner, args)));
    } finally {
      subLock.readLock().unlock();
    }
  }

  public void setupAsyncOperation() {
    if (threadPool == null) {
      synchronized (poolLock) {
        if (threadPool == null) {
          threadPool = Executors.newCachedThreadPool();
        }
      }
    }
  }

  public synchronized void shutdown() {
    if (threadPool == null) {
      return;
    }
    threadPool.shutdown();
  }
}
