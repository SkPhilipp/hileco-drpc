package machine.lib.message.proxy;

public class ProxyNetworkTopics {

    public static String getTopic(Class<?> remoteClass) {
        return remoteClass.getName();
    }

    public static <P> String getTopic(Class<?> remoteClass, P binding) {
        return String.format("%s/%s", remoteClass.getName(), binding);
    }

}
