package domain.dto;

import java.io.Serializable;

public class DrugDTO implements Serializable {
    private Integer id;
    private Boolean selected;
    private String name;
    private String description;
    private Integer inStock;
    private Integer toOrder;

    public DrugDTO(Integer id, Boolean selected, String name, String description, Integer inStock, Integer toOrder) {
        this.id = id;
        this.selected = selected;
        this.name = name;
        this.description = description;
        this.inStock = inStock;
        this.toOrder = toOrder;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getInStock() {
        return inStock;
    }

    public void setInStock(Integer inStock) {
        this.inStock = inStock;
    }

    public Integer getToOrder() {
        return toOrder;
    }

    public void setToOrder(Integer toOrder) {
        this.toOrder = toOrder;
    }
}
