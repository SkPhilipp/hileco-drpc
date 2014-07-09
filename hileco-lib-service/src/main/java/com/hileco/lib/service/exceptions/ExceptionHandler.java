package com.hileco.lib.service.exceptions;

import com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;

/**
 * Can handle every kind of exception, when an exception is an instance of {@link IllegalArgumentException},
 * it is regarded as being a functional error.
 * <p/>
 * Every other kind of exception will be regarded as a technical error.
 */
@Provider
public class ExceptionHandler implements ExceptionMapper<Exception> {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public Response toResponse(Exception exception) {
        RawException exceptionMessage = new RawException(exception);
        if (exception instanceof IllegalArgumentException) {
            LOG.warn("{}: {}", exception.getClass().getSimpleName(), exception.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(exceptionMessage).build();
        } else {
            LOG.error(exception.getMessage(), exception);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON_TYPE).entity(exceptionMessage).build();
        }
    }

    @SuppressWarnings("unused")
    private static class RawException implements Serializable {

        private String message;
        private String cause;
        private String exception;

        public RawException(Exception exception) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PrintStream writer = new PrintStream(byteArrayOutputStream);
            exception.printStackTrace(writer);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            this.exception = new String(bytes, Charsets.UTF_8);
            Throwable rootCause = exception;
            if (exception.getCause() != null) {
                rootCause = exception.getCause();
            }
            this.message = exception.getMessage();
            this.cause = rootCause.getMessage();
        }

        public String getException() {
            return exception;
        }

        public String getMessage() {
            return message;
        }

        public String getCause() {
            return cause;
        }

    }

}