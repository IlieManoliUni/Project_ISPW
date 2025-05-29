package ispw.project.project_ispw.dao.memory;

import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.dao.ListDao;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ListDaoInMemory implements ListDao {

    private static final Logger LOGGER = Logger.getLogger(ListDaoInMemory.class.getName());

    private final Map<Integer, ListBean> listMap = new HashMap<>();

    private final Map<String, Set<Integer>> userListsMap = new HashMap<>();

    @Override
    public ListBean retrieveById(int id) throws ExceptionDao {
        return listMap.get(id);
    }

    @Override
    public void saveList(ListBean list, UserBean user) throws ExceptionDao {
        if (list == null || user == null) {
            throw new IllegalArgumentException("List and User cannot be null.");
        }

        int id = list.getId();
        String username = user.getUsername();

        if (listMap.containsKey(id)) {
            throw new ExceptionDao("List with ID " + id + " already exists.");
        }

        listMap.put(id, list);

        userListsMap.computeIfAbsent(username, k -> new HashSet<>()).add(id);
    }

    @Override
    public void deleteList(ListBean list) throws ExceptionDao {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null.");
        }

        int id = list.getId();
        String username = list.getUsername();

        if (!listMap.containsKey(id)) {
            throw new ExceptionDao("List with ID " + id + " not found for deletion.");
        }

        listMap.remove(id);

        Set<Integer> userLists = userListsMap.get(username);
        if (userLists != null) {
            userLists.remove(id);
            if (userLists.isEmpty()) {
                userListsMap.remove(username);
            }
        }
    }

    @Override
    public List<ListBean> retrieveAllListsOfUsername(String username) throws ExceptionDao {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null.");
        }

        List<ListBean> result = new ArrayList<>();
        Set<Integer> ids = userListsMap.get(username);

        if (ids != null) {
            for (int id : ids) {
                ListBean list = listMap.get(id);
                if (list != null) {
                    result.add(list);
                } else {
                    LOGGER.log(Level.WARNING, "List ID {0} found for user {1} but not in main list map. Data inconsistency.", new Object[]{id, username});
                }
            }
        }
        return Collections.unmodifiableList(result);
    }
}