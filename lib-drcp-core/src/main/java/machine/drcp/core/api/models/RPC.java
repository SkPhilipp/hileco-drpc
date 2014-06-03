package machine.drcp.core.api.models;

/**
 * An RPC description, containing a method reference and arguments to be passed to it.
 */
public class RPC {

    private String method;
    private Object[] arguments;

    public RPC(String method, Object[] arguments) {
        this.method = method;
        this.arguments = arguments;
    }

    public RPC() {
    }

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
