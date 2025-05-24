package ispw.project.project_ispw.dao.inMemory;

import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.dao.ListDao;
import ispw.project.project_ispw.exception.ExceptionDao; // Assuming you have this custom exception

import java.util.ArrayList;
import java.util.Collections; // For unmodifiable list
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListDaoInMemory implements ListDao {

    // Store lists by ID
    private final Map<Integer, ListBean> listMap = new HashMap<>();

    // Store list IDs by username
    private final Map<String, Set<Integer>> userListsMap = new HashMap<>();

    @Override
    public ListBean retrieveById(int id) throws ExceptionDao {
        ListBean list = listMap.get(id);
        if (list == null) {
            // Throw ExceptionDao if the list is not found, consistent with CSV DAOs
            throw new ExceptionDao("No List Found with ID: " + id);
        }
        return list;
    }

    @Override
    public void saveList(ListBean list, UserBean user) throws ExceptionDao {
        if (list == null || user == null) {
            throw new IllegalArgumentException("List and User cannot be null.");
        }

        int id = list.getId();
        String username = user.getUsername();

        if (listMap.containsKey(id)) {
            // Throw ExceptionDao if the list already exists
            throw new ExceptionDao("List with ID " + id + " already exists.");
        }

        listMap.put(id, list);

        // Ensure the set for the username exists, then add the list ID
        userListsMap.computeIfAbsent(username, k -> new HashSet<>()).add(id);
    }

    @Override
    public void deleteList(ListBean list) throws ExceptionDao {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null.");
        }

        int id = list.getId();
        String username = list.getUsername();

        // Check if the list exists before attempting to remove
        if (!listMap.containsKey(id)) {
            throw new ExceptionDao("List with ID " + id + " not found for deletion.");
        }

        // Remove the list from the main map
        listMap.remove(id);

        // Remove the list ID from the user's set of lists
        Set<Integer> userLists = userListsMap.get(username);
        if (userLists != null) {
            userLists.remove(id);
            // Clean up if the user no longer has any lists
            if (userLists.isEmpty()) {
                userListsMap.remove(username);
            }
        }
        // No exception thrown if list isn't linked to user, as the primary removal is from listMap.
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
                // Ensure the list still exists in listMap (data integrity check)
                ListBean list = listMap.get(id);
                if (list != null) {
                    result.add(list);
                } else {
                    // Log a warning if a list ID is found in userListsMap but not in listMap
                    System.err.println("Warning: List ID " + id + " found for user " + username + " but not in main list map. Data inconsistency.");
                }
            }
        }

        if (result.isEmpty()) {
            // Throw ExceptionDao if no lists are found for the username
            throw new ExceptionDao("No Lists Found for username: " + username);
        }

        // Return an unmodifiable list to prevent external modification of the internal state
        return Collections.unmodifiableList(result);
    }
}