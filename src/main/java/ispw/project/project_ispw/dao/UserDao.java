package ispw.project.project_ispw.dao;


import ispw.project.project_ispw.bean.UserBean;

public interface UserDao {

    UserBean retrieveByUsername(String username) throws Exception;

    void saveUser(UserBean user) throws Exception;
}
