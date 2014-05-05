package machine.humanity.api.services;

import machine.humanity.api.domain.HarvesterStatus;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/generators")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneratorService {

    /**
     * Retrieves the status of a source generator.
     *
     * @param source
     * @return
     */
    @GET
    @Path("/status")
    public HarvesterStatus status(@QueryParam("source") String source);

    /**
     * Initiates harvesting and training of a source.
     *
     * Status of {@link HarvesterStatus#NONE} is required.
     *
     * @param source
     */
    @GET
    @Path("/harvest")
    public HarvesterStatus harvest(@QueryParam("source") String source);

    /**
     * Generates given amount of lines using a given source generator.
     *
     * Status of {@link HarvesterStatus#HARVESTED} is required.
     *
     * @param source
     * @param amount
     */
    @GET
    @Path("/generate")
    public List<String> generate(@QueryParam("source") String source, @QueryParam("amount") Integer amount);

}
