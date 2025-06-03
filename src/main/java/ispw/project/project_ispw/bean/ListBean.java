package ispw.project.project_ispw.bean;

import java.io.Serializable;
import java.util.Objects;

public class ListBean implements Serializable{

    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String username;

    public ListBean() {
    }

    public ListBean(int id, String name, String username) {
        this.id = id;
        this.name = name;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListBean listBean = (ListBean) o;
        return id == listBean.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ListBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
