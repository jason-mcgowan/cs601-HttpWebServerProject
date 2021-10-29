package httpserver.util;

import java.util.function.BiConsumer;

public interface EventListener<A extends EventArg> {
  BiConsumer<Object, A> getSubscriber();
}
