package machine.humanity.services;

import machine.humanity.harvesting.HarvesterStatus;

import java.util.List;

public interface GeneratorService {

    /**
     * Retrieves the status of a source generator.
     *
     * @param source
     * @return
     */
    public HarvesterStatus status(String source);

    /**
     * Initiates harvesting and training of a source.
     *
     * Status of {@link machine.humanity.harvesting.HarvesterStatus#NONE} is required.
     *
     * @param source
     */
    public HarvesterStatus harvest(String source);

    /**
     * Generates given amount of lines using a given source generator.
     *
     * Status of {@link machine.humanity.harvesting.HarvesterStatus#HARVESTED} is required.
     *
     * @param source
     * @param amount
     */
    public List<String> generate(String source, Integer amount);

}
