package machine.management.services.lib.dao;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.UUID;

/**
 * Defines the properties of a model; The bare minimum being that a model must be uniquely identifyable and {@link Serializable}.
 */
@MappedSuperclass
public class Model implements Serializable {

    @Id
    @Type(type = "uuid-char")
    @Column(name = "id")
    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}
