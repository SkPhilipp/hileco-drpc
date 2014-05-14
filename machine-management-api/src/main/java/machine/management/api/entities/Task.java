package machine.management.api.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@SuppressWarnings("unused")
@Entity
@Table(name = Task.TASK)
public class Task extends Model {

    public static final String BOTTABLE = "bottable";
    public static final String TASK = "task";
    public static final String TYPE = "type";
    public static final String STATUS = "status";

    @Column(name = BOTTABLE)
    private String bottable;
    @Column(name = TYPE)
    private String type;
    @Column(name = STATUS)
    private TaskStatus status;

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
