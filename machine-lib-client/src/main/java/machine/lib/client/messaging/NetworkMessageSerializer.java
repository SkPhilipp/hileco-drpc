package machine.lib.client.messaging;

import com.google.common.base.Charsets;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;

public interface NetworkMessageSerializer {

    public static Charset CHARSET = Charsets.UTF_8;

    public String serialize(NetworkMessage<?> networkMessage) throws IOException;

    public byte[] serializeToBytes(NetworkMessage<?> networkMessage) throws IOException;

    public <T extends Serializable> NetworkMessage<T> derialize(String serializedNetworkmessage, Class<NetworkMessage<T>> type) throws IOException;

    public <T extends Serializable> NetworkMessage<T> derializeFromBytes(byte[] serializedNetworkmessage, Class<NetworkMessage<T>> type) throws IOException;

}
