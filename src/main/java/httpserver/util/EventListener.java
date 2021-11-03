package httpserver.util;

import java.util.function.BiConsumer;

/**
 * Provides a convenient way to reference event listeners by pointing to their delegate method.
 *
 * @author Jason McGowan
 */
public interface EventListener<A> {

  BiConsumer<Object, A> getSubscriber();
}
