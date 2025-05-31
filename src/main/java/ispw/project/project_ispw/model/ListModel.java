// ispw.project.project_ispw.model.ListModel
package ispw.project.project_ispw.model;

import ispw.project.project_ispw.bean.ListBean; // Import your ListBean
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ListModel {

    private final ListBean listBean; // The underlying ListBean (the actual data)

    // Observable properties for UI binding
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty username;
    private final StringProperty displayString; // A combined string for convenience


    public ListModel(ListBean listBean) {
        if (listBean == null) {
            throw new IllegalArgumentException("ListBean cannot be null for ListModel.");
        }
        this.listBean = listBean;

        // Initialize properties from the ListBean
        this.id = new SimpleIntegerProperty(listBean.getId());
        this.name = new SimpleStringProperty(listBean.getName());
        this.username = new SimpleStringProperty(listBean.getUsername());

        // Create a display string property (optional, but convenient for ListView)
        this.displayString = new SimpleStringProperty(
                "List: " + listBean.getName() + " (ID: " + listBean.getId() + ")"
        );
        // If you had an item count, you might initialize it here too
        // this.itemCount = new SimpleIntegerProperty(0); // Initialize, then update if needed
    }

    // --- Getters for observable properties ---
    public IntegerProperty idProperty() {
        return id;
    }
    public int getId() {
        return id.get();
    }

    public StringProperty nameProperty() {
        return name;
    }
    public String getName() {
        return name.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }
    public String getUsername() {
        return username.get();
    }

    public StringProperty displayStringProperty() {
        return displayString;
    }
    public String getDisplayString() {
        return displayString.get();
    }

    // You can add a method to get the underlying ListBean
    public ListBean getListBean() {
        return listBean;
    }

    // Optional: Override toString for simpler debugging or default ListCell display
    @Override
    public String toString() {
        return getDisplayString();
    }
}