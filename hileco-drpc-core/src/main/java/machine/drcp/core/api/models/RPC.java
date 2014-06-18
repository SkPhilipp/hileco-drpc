package machine.drcp.core.api.models;

/**
 * An RPC description, containing a method reference and params to be passed to it.
 */
public class RPC {

    private String method;
    private Object[] params;

    public RPC(String method, Object[] params) {
        this.method = method;
        this.params = params;
    }

    public RPC() {
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

}
