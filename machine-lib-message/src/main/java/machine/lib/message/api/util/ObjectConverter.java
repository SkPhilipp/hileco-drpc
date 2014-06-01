package machine.lib.message.api.util;

public interface ObjectConverter {

    public <R> R convert(Object object, Class<R> target);

}
