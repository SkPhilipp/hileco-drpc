package machine.humanity.services;

import machine.humanity.harvesting.HarvesterStatus;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

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
    public HarvesterStatus status(String source);

    /**
     * Initiates harvesting and training of a source.
     *
     * Status of {@link machine.humanity.harvesting.HarvesterStatus#NONE} is required.
     *
     * @param source
     */
    @GET
    @Path("/harvest")
    public HarvesterStatus harvest(String source);

    /**
     * Generates given amount of lines using a given source generator.
     *
     * Status of {@link machine.humanity.harvesting.HarvesterStatus#HARVESTED} is required.
     *
     * @param source
     * @param amount
     */
    @GET
    @Path("/generate")
    public List<String> generate(String source, Integer amount);

}
