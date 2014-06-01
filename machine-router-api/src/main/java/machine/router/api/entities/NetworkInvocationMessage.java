package machine.router.api.entities;

public class NetworkInvocationMessage {

    private String method;
    private Object[] arguments;
    private boolean report;

    public NetworkInvocationMessage(String method, Object[] arguments, boolean report) {
        this.method = method;
        this.arguments = arguments;
        this.report = report;
    }

    public NetworkInvocationMessage() {
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

    public boolean isReport() {
        return report;
    }

    public void setReport(boolean report) {
        this.report = report;
    }

}
