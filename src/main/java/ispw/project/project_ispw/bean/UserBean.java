package ispw.project.project_ispw.bean;

import java.io.Serializable;
import java.util.Objects;

public class UserBean implements Serializable{

    private static final long serialVersionUID = 1L;

    private String username;
    private String password;

    public UserBean() {
    }

    public UserBean(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // For the InMemory Operations
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserBean userBean = (UserBean) o;
        return Objects.equals(username, userBean.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "username='" + username + '\'' +
                // It's generally not recommended to include sensitive information like passwords in toString()
                // ", password='" + password + '\'' +
                '}';
    }
}
