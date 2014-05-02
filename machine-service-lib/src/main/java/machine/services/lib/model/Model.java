package machine.services.lib.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Defines the properties of a model; The bare minimum being that a model must be uniquely identifyable and {@link Serializable}.
 */
public interface Model extends Serializable {

    public UUID getId();

    public void setId(UUID id);

}
