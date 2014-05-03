package machine.management.model;

import machine.management.services.lib.model.Model;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "definition")
public class Definition implements Model {

    @Id
    @Type(type = "uuid-char")
    @Column(name = "id")
    private UUID id;
    @Column(name = "bottable")
    private String bottable;
    @Column(name = "type")
    private String type;
    @Column(name = "format")
    private String format;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBottable() {
        return bottable;
    }

    public void setBottable(String bottable) {
        this.bottable = bottable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

}
