package domain.dto;

import java.io.Serializable;
import java.sql.Date;

public class OrderDTO implements Serializable {
    private Integer id;
    private String orderedBy;
    private Boolean delivered;
    private Date orderedAt;
    private Date deliveredAt;

    public OrderDTO(Integer id, String orderedBy, Boolean delivered, Date orderedAt, Date deliveredAt) {
        this.id = id;
        this.orderedBy = orderedBy;
        this.delivered = delivered;
        this.orderedAt = orderedAt;
        this.deliveredAt = deliveredAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderedBy() {
        return orderedBy;
    }

    public void setOrderedBy(String orderedBy) {
        this.orderedBy = orderedBy;
    }

    public Boolean getDelivered() {
        return delivered;
    }

    public void setDelivered(Boolean delivered) {
        this.delivered = delivered;
    }

    public Date getOrderedAt() {
        return orderedAt;
    }

    public void setOrderedAt(Date orderedAt) {
        this.orderedAt = orderedAt;
    }

    public Date getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Date deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
}
