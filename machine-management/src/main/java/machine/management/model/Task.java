package machine.management.model;

import machine.services.lib.model.Model;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "task")
public class Task implements Model {

    @Id
    @Type(type = "uuid-char")
    @Column(name = "id")
    private UUID id;
    @Column(name = "bottable")
    private String bottable;
    @Column(name = "type")
    private String type;
    @Column(name = "status")
    private TaskStatus status;

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

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

}
