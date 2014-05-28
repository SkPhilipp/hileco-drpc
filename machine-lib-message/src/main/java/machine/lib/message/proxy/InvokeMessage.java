package machine.lib.message.proxy;

import java.io.Serializable;

public class InvokeMessage implements Serializable {

    private String method;
    private Object[] arguments;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

}
