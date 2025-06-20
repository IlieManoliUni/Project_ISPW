package ispw.project.project_ispw.controller.application.util;

import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.dao.UserDao;
import ispw.project.project_ispw.exception.ExceptionApplication;

public class AuthService {

    private final UserDao userDao;
    private UserBean currentUser;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserBean getCurrentUser() {
        return currentUser;
    }

    public boolean login(String username, String password) throws ExceptionApplication {
        try {
            UserBean user = userDao.retrieveByUsername(username);
            if (user != null && user.getPassword().equals(password)) {
                this.currentUser = user;
                return true;
            } else {
                this.currentUser = null;
                return false;
            }
        } catch (Exception e) {
            throw new ExceptionApplication("Failed to login: " + e.getMessage(), e);
        }
    }

    public boolean registerUser(UserBean userBean) throws ExceptionApplication {
        try {
            if (userDao.retrieveByUsername(userBean.getUsername()) != null) {
                throw new ExceptionApplication("Username already exists. Please choose a different one.");
            }
            userDao.saveUser(userBean);
            return true;
        } catch (ExceptionApplication e) {
            throw e;
        } catch (Exception e) {
            throw new ExceptionApplication("Failed to register user: " + e.getMessage(), e);
        }
    }

    public void logout() {
        this.currentUser = null;
    }
}