package ispw.project.project_ispw.dao;


import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.exception.ExceptionDao;

public interface UserDao {

    UserBean retrieveByUsername(String username) throws ExceptionDao;

    void saveUser(UserBean user) throws ExceptionDao;
}
