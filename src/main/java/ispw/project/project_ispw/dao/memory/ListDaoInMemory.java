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
import java.util.Objects;
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
        if (list == null || user == null || user.getUsername() == null) {
            throw new IllegalArgumentException("List, User, or Username cannot be null.");
        }

        int id = list.getId();
        String username = user.getUsername();

        if (listMap.containsKey(id)) {
            throw new ExceptionDao("List with ID " + id + " already exists. Use update if you intend to modify.");
        }

        if (!Objects.equals(list.getUsername(), username)) {
            LOGGER.log(Level.WARNING, "ListBean''s username ({0}) does not match UserBean''s username ({1}). Setting ListBean''s username.", new Object[]{list.getUsername(), username});
            list.setUsername(username);
        }

        listMap.put(id, list);

        userListsMap.computeIfAbsent(username, k -> new HashSet<>()).add(id);

        LOGGER.log(Level.INFO, "List with ID {0} saved for user {1}.", new Object[]{id, username});
    }

    @Override
    public void deleteList(ListBean list) throws ExceptionDao {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null.");
        }

        int id = list.getId();
        String listOwnerUsername = list.getUsername();

        if (listOwnerUsername == null) {
            throw new IllegalArgumentException("Cannot delete list: ListBean's username is null. Ensure the ListBean is fully populated.");
        }

        if (!listMap.containsKey(id)) {
            throw new ExceptionDao("List with ID " + id + " not found for deletion.");
        }

        ListBean storedList = listMap.get(id);
        if (!Objects.equals(storedList.getUsername(), listOwnerUsername)) {
            LOGGER.log(Level.WARNING, "Attempt to delete list {0} by user {1}, but it''s owned by {2}.",
                    new Object[]{id, listOwnerUsername, storedList.getUsername()});
            throw new ExceptionDao("User " + listOwnerUsername + " does not own list with ID " + id + ".");
        }

        listMap.remove(id);

        Set<Integer> userLists = userListsMap.get(listOwnerUsername);
        if (userLists != null) {
            userLists.remove(id);
            if (userLists.isEmpty()) {
                userListsMap.remove(listOwnerUsername);
                LOGGER.log(Level.INFO, "User {0} no longer has any lists mapped, removing user entry.", listOwnerUsername);
            }
        }
        LOGGER.log(Level.INFO, "List with ID {0} deleted for user {1}.", new Object[]{id, listOwnerUsername});
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
                    LOGGER.log(Level.WARNING, "List ID {0} found for user {1} in userListsMap but not in main listMap. Data inconsistency detected.", new Object[]{id, username});
                }
            }
        }
        return Collections.unmodifiableList(result);
    }
}