package machine.management.api.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@SuppressWarnings("unused")
@Entity
@Table(name = Definition.DEFINITION)
public class Definition extends Model {

    public static final String DEFINITION = "definition";
    public static final String BOTTABLE = "bottable";
    public static final String TYPE = "type";
    public static final String FORMAT = "format";

    @Column(name = BOTTABLE)
    private String bottable;
    @Column(name = TYPE)
    private String type;
    @Column(name = FORMAT)
    private String format;

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
