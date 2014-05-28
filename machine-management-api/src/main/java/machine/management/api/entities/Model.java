package machine.management.api.entities;

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

    public static final String ID = "id";
    @Id
    @Type(type = "uuid-char")
    @Column(name = ID)
    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}
