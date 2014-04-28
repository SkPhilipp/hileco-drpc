package machine.management.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "Definition")
public class Definition implements Model {

    @Column(name = "bottable")
    private String bottable;
    @Column(name = "type")
    private String type;
    @Column(name = "schema")
    private String schema;
    @Id
    private UUID id;

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

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

}
