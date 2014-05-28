package machine.lib.message.api;

/**
 * Specifies the implementation is a representation of an "object" which can be made available using a
 * {@link NetworkConnector}, or is available elsewhere on the {@link Network}.
 *
 * @param <T> type of object identifier, {@link T#toString()} is used to build the object's topic.
 */
public interface NetworkObject<T> {
}
