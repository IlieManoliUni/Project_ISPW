package ispw.project.project_ispw.model;

import ispw.project.project_ispw.bean.ListBean;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ListModel {

    private final ListBean listBean;

    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty username;
    private final StringProperty displayString;


    public ListModel(ListBean listBean) {
        if (listBean == null) {
            throw new IllegalArgumentException("ListBean cannot be null for ListModel.");
        }
        this.listBean = listBean;

        this.id = new SimpleIntegerProperty(listBean.getId());
        this.name = new SimpleStringProperty(listBean.getName());
        this.username = new SimpleStringProperty(listBean.getUsername());

        this.displayString = new SimpleStringProperty(
                "List: " + listBean.getName() + " (ID: " + listBean.getId() + ")"
        );
    }

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

    public ListBean getListBean() {
        return listBean;
    }

    @Override
    public String toString() {
        return getDisplayString();
    }
}