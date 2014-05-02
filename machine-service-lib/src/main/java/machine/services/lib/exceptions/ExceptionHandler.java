package machine.services.lib.exceptions;

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

    public Response handleFunctional(Exception exception) {
        RawException exceptionMessage = new RawException();
        exceptionMessage.setException(exception);
        return Response.status(Response.Status.BAD_REQUEST).entity(exceptionMessage).build();
    }

    public Response handleTechnical(Exception exception) {
        RawException exceptionMessage = new RawException();
        exceptionMessage.setException(exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(exceptionMessage).build();
    }

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof IllegalArgumentException) {
            return this.handleFunctional(exception);
        } else {
            return this.handleTechnical(exception);
        }
    }

}