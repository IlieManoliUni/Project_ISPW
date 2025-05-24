package ispw.project.project_ispw.dao;



import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.UserBean;

import java.util.List;

public interface ListDao {

    // Method to retrieve a List by its ID
    ListBean retrieveById(int id) throws Exception;

    // Method to save a List for a User
    void saveList(ListBean list, UserBean user) throws Exception;

    // Method to delete a List
    void deleteList(ListBean list) throws Exception;

    // Method to retrieve all Lists for a given username
    List<ListBean> retrieveAllListsOfUsername(String username) throws Exception;
}
