package machine.management.services.lib.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

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
        RawException exceptionMessage = new RawException();
        exceptionMessage.setException(exception);
        if (exception instanceof IllegalArgumentException) {
            LOG.warn("Handling a functional exception:", exception);
            return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(exceptionMessage).build();
        } else {
            LOG.error("Handling a technical exception:", exception);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON_TYPE).entity(exceptionMessage).build();
        }
    }

}