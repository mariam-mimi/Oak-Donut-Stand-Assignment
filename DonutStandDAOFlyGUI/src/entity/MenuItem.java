
package entity;

/**
 * Represents a menu item in Oak Donuts.
 */
public class MenuItem {
    private int id;
    private String name;
    private String category;
    private double unitPrice;

    public MenuItem(int id, String name, String category, double unitPrice) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.unitPrice = unitPrice;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public String toString() {
        // This is what will show in the JList in the GUI.
        return String.format("%s — $%.2f", name, unitPrice);
    }
}
