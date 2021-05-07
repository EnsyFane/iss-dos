package domain.models;

public class Drug extends Entity<Integer> {
    private String name;
    private String description;
    private Integer inStock;

    public Drug() {

    }

    public Drug(Integer id, String name, String description, Integer inStock) {
        super(id);
        this.name = name;
        this.description = description;
        this.inStock = inStock;
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
}
