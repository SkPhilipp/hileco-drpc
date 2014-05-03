package machine.management.services.lib.exceptions;

import com.google.common.base.Charsets;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * A simple class containing an exception field.
 */
public class RawException {

    private String message;
    private String cause;
    private String exception;

    public String getException() {
        return exception;
    }

    public String getMessage() {
        return message;
    }

    public String getCause() {
        return cause;
    }

    public void setException(Exception exception) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream writer = new PrintStream(byteArrayOutputStream);
        exception.printStackTrace(writer);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        this.exception = new String(bytes, Charsets.UTF_8);
        Throwable rootCause = exception;
        if(exception.getCause() != null){
            rootCause = exception.getCause();
        }
        this.message = exception.getMessage();
        this.cause = rootCause.getMessage();
    }

}
