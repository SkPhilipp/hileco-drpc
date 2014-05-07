package machine.lib.service;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.File;
import java.net.URISyntaxException;

@Path("/")
public class Index {

    @GET
    @Produces("text/html")
    public Response index() throws URISyntaxException {
        File f = new File(System.getProperty("user.dir") + "/index.html");
        String mt = new MimetypesFileTypeMap().getContentType(f);
        return Response.ok(f, mt).build();
    }

    @GET
    @Path("/hello")
    public Response helloGet() {
        return Response.status(200).entity("HTTP GET method called").build();
    }

    @POST
    @Path("/hello")
    public Response helloPost() {
        return Response.status(200).entity("HTTP POST method called").build();
    }

}
