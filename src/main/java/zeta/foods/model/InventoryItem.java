package zeta.foods.model;

public class InventoryItem {
    private Long id;
    private String name;
    private int quantity;
    private String unit;

    public InventoryItem() {}

    public InventoryItem(Long id, String name, int quantity, String unit) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}