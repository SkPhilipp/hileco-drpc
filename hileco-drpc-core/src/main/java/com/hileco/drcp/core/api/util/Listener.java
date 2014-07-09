package com.hileco.drcp.core.api.util;

public abstract class Listener implements SilentCloseable {

    private SilentCloseable listener;

    protected abstract SilentCloseable listen();

    public void start() {
        if (this.listener != null) {
            throw new IllegalStateException("Already listening");
        }
        this.listener = this.listen();
    }

    public void close() {
        if (this.listener != null) {
            this.listener.close();
            this.listener = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

}
