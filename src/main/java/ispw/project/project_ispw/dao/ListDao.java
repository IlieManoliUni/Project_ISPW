package ispw.project.project_ispw.dao;



import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.util.List;

public interface ListDao {

    ListBean retrieveById(int id) throws ExceptionDao;

    void saveList(ListBean list, UserBean user) throws ExceptionDao;

    void deleteList(ListBean list) throws ExceptionDao;

    List<ListBean> retrieveAllListsOfUsername(String username) throws ExceptionDao;
}
