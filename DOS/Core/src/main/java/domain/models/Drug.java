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

    public static class Builder {
        private Integer _id;
        private String _name;
        private String _description;
        private Integer _inStock;

        public Builder() {
            _id = 0;
            _name = "drug-name";
            _description = "description";
            _inStock = 26;
        }

        public Builder from(Drug other) {
            _id = other.getId();
            _name = other.getName();
            _description = other.getDescription();
            _inStock = other.getInStock();
            return this;
        }

        public Builder withId(Integer id) {
            _id = id;
            return this;
        }

        public Builder withName(String name) {
            _name = name;
            return this;
        }

        public Builder withDescription(String description) {
            _description = description;
            return this;
        }

        public Builder withInStock(Integer inStock) {
            _inStock = inStock;
            return this;
        }

        public Drug build() {
            var drug = new Drug();
            drug.setId(_id);
            drug.setName(_name);
            drug.setDescription(_description);
            drug.setInStock(_inStock);

            return drug;
        }
    }
}
