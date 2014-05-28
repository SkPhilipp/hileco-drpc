package machine.lib.message.proxy;

import java.io.Serializable;

public class MethodCallMessage implements Serializable {

    private String method;
    private Object[] args;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

}
