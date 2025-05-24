package ispw.project.project_ispw.controller.application.util;

import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.dao.UserDao;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthService {

    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());
    private final UserDao userDao;
    private UserBean currentUser; // AuthService can manage its own internal current user

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserBean getCurrentUser() {
        return currentUser;
    }

    public boolean login(String username, String password) throws ExceptionApplicationController {
        try {
            UserBean user = userDao.retrieveByUsername(username);
            if (user != null && user.getPassword().equals(password)) {
                this.currentUser = user; // Set internal current user
                LOGGER.log(Level.INFO, "User {0} logged in successfully.", username);
                return true;
            } else {
                LOGGER.log(Level.WARNING, "Login failed for user: {0}", username);
                return false;
            }
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "DAO error during login for user: " + username, e);
            throw new ExceptionApplicationController("Failed to login: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during login for user: " + username, e);
            throw new ExceptionApplicationController("Failed to login: " + e.getMessage(), e);
        }
    }

    public boolean registerUser(UserBean userBean) throws ExceptionApplicationController {
        try {
            if (userDao.retrieveByUsername(userBean.getUsername()) != null) {
                LOGGER.log(Level.WARNING, "Registration failed: Username ''{0}'' already exists.", userBean.getUsername());
                throw new ExceptionApplicationController("Username already exists. Please choose a different one.");
            }
            userDao.saveUser(userBean);
            LOGGER.log(Level.INFO, "User {0} registered successfully.", userBean.getUsername());
            return true;
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "DAO error during user registration for user: " + userBean.getUsername(), e);
            throw new ExceptionApplicationController("Failed to register user: " + e.getMessage(), e);
        } catch (ExceptionApplicationController e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during user registration for user: " + userBean.getUsername(), e);
            throw new ExceptionApplicationController("Failed to register user: " + e.getMessage(), e);
        }
    }
}