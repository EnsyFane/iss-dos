package domain.models;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Order extends Entity<Integer> {
    private Integer orderedBy;
    private Boolean delivered;
    private Date orderedAt;
    private Date deliveredAt;
    private Map<Integer, Integer> drugs;

    public Order() {

    }

    public Order(Integer id, Integer orderedBy, Boolean delivered, Date orderedAt, Date deliveredAt) {
        super(id);
        this.orderedBy = orderedBy;
        this.delivered = delivered;
        this.orderedAt = orderedAt;
        this.deliveredAt = deliveredAt;

        drugs = new HashMap<>();
    }

    public Integer getOrderedBy() {
        return orderedBy;
    }

    public void setOrderedBy(Integer orderedBy) {
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

    public Map<Integer, Integer> getDrugs() {
        return drugs;
    }

    public void setDrugs(Map<Integer, Integer> drugs) {
        this.drugs = new HashMap<>(drugs);
    }

    public void addDrug(Integer drugId, Integer quantity) {
        var hasKey = this.drugs.containsKey(drugId);
        if (hasKey) {
            this.drugs.put(drugId, this.drugs.get(drugId) + quantity);
        } else {
            this.drugs.put(drugId, quantity);
        }
    }

    public void removeDrug(Integer drugId) {
        this.drugs.remove(drugId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return getOrderedBy().equals(order.getOrderedBy()) &&
                getDelivered().equals(order.getDelivered()) &&
                getOrderedAt().equals(order.getOrderedAt()) &&
                getDeliveredAt().equals(order.getDeliveredAt()) &&
                getDrugs().equals(order.getDrugs());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrderedBy(), getDelivered(), getOrderedAt(), getDeliveredAt(), getDrugs());
    }

    public static class Builder {
        private Integer _id;
        private Integer _orderedBy;
        private Boolean _delivered;
        private Date _orderedAt;
        private Date _deliveredAt;
        private Map<Integer, Integer> _drugs;

        public Builder() {
            _id = 0;
            _orderedBy = 0;
            _delivered = true;
            _orderedAt = new Date(System.currentTimeMillis() - 10);
            _deliveredAt = new Date(System.currentTimeMillis() + 10);
            _drugs = new HashMap<>();
        }

        public Builder from(Order other) {
            _id = other.getId();
            _orderedBy = other.getOrderedBy();
            _delivered = other.getDelivered();
            _orderedAt = other.getOrderedAt();
            _deliveredAt = other.getDeliveredAt();
            _drugs = new HashMap<>(other.getDrugs());
            return this;
        }

        public Builder withId (Integer id) {
            _id = id;
            return this;
        }

        public Builder withOrderedBy (Integer orderedBy) {
            _orderedBy = orderedBy;
            return this;
        }

        public Builder withDelivered (Boolean delivered) {
            _delivered = delivered;
            return this;
        }

        public Builder withOrderedAt (Date orderedAt) {
            _orderedAt = orderedAt;
            return this;
        }

        public Builder withDeliveredAt (Date deliveredAt) {
            _deliveredAt = deliveredAt;
            return this;
        }

        public Builder withDrugs (Map<Integer, Integer> drugs) {
            _drugs = new HashMap<>(drugs);
            return this;
        }

        public Order build() {
            var order = new Order();
            order.setId(_id);
            order.setOrderedBy(_orderedBy);
            order.setDelivered(_delivered);
            order.setOrderedAt(_orderedAt);
            order.setDeliveredAt(_deliveredAt);
            order.setDrugs(_drugs);

            return order;
        }
    }
}
