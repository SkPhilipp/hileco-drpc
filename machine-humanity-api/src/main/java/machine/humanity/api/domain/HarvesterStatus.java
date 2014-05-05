package machine.humanity.api.domain;

public enum HarvesterStatus {

    /**
     * Indicates the board has not yet been harvested or trained.
     */
    NONE,
    /**
     * Indicates the board's contents are being harvested.
     */
    HARVESTING,
    /**
     * Indicates harvesting succeeded, and the board is available as a file for training.
     */
    HARVESTED,
    /**
     * Indicates harvesting failed.
     */
    ERRED

}
