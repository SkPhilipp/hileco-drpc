package machine.drcp.core.api.entities;

/**
 * An RPC message, containing a method reference and arguments to be passed to it.
 */
public class RPCMessage {

    private String method;
    private Object[] arguments;

    public RPCMessage(String method, Object[] arguments) {
        this.method = method;
        this.arguments = arguments;
    }

    public RPCMessage() {
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
