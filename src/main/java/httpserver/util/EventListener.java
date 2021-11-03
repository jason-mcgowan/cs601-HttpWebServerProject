package httpserver.util;

import java.util.function.BiConsumer;

public interface EventListener<A> {
  BiConsumer<Object, A> getSubscriber();
}
