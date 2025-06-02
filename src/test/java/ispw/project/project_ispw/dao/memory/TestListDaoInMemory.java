package ispw.project.project_ispw.dao.memory;

import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.exception.ExceptionDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TestListDaoInMemory {

    private ListDaoInMemory listDaoInMemory;

    @BeforeEach
    void setUp() {
        listDaoInMemory = new ListDaoInMemory();
    }

    @Test
    @DisplayName("saveList: Should successfully save a new list for a user")
    void testSaveList_Success() throws ExceptionDao {
        UserBean user = new UserBean("user1", "pass");
        ListBean list = new ListBean(1, "My First List", "user1");

        listDaoInMemory.saveList(list, user);

        ListBean retrievedList = listDaoInMemory.retrieveById(1);
        assertNotNull(retrievedList);
        assertEquals(1, retrievedList.getId());
        assertEquals("My First List", retrievedList.getName());
        assertEquals("user1", retrievedList.getUsername());

        List<ListBean> user1Lists = listDaoInMemory.retrieveAllListsOfUsername("user1");
        assertFalse(user1Lists.isEmpty());
        assertEquals(1, user1Lists.size());
        assertEquals("My First List", user1Lists.get(0).getName());
    }

    @Test
    @DisplayName("saveList: Should throw ExceptionDao if list ID already exists")
    void testSaveList_DuplicateId() throws ExceptionDao {
        UserBean user = new UserBean("user1", "pass");
        ListBean list1 = new ListBean(1, "List One", "user1");
        ListBean list2 = new ListBean(1, "List Two", "user1");

        listDaoInMemory.saveList(list1, user);

        ExceptionDao thrown = assertThrows(ExceptionDao.class, () -> listDaoInMemory.saveList(list2, user));
        assertTrue(thrown.getMessage().contains("List with ID 1 already exists."));
    }

    @Test
    @DisplayName("saveList: Should throw IllegalArgumentException if list is null")
    void testSaveList_NullList() {
        UserBean user = new UserBean("user1", "pass");
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> listDaoInMemory.saveList(null, user));
        assertEquals("List and User cannot be null.", thrown.getMessage());
    }

    @Test
    @DisplayName("saveList: Should throw IllegalArgumentException if user is null")
    void testSaveList_NullUser() {
        ListBean list = new ListBean(1, "My List", "user1");
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> listDaoInMemory.saveList(list, null));
        assertEquals("List and User cannot be null.", thrown.getMessage());
    }

    @Test
    @DisplayName("saveList: Should associate multiple lists with the same user")
    void testSaveList_MultipleListsForOneUser() throws ExceptionDao {
        UserBean user = new UserBean("multiuser", "pass");
        ListBean list1 = new ListBean(101, "Multi List A", "multiuser");
        ListBean list2 = new ListBean(102, "Multi List B", "multiuser");

        listDaoInMemory.saveList(list1, user);
        listDaoInMemory.saveList(list2, user);

        List<ListBean> retrievedLists = listDaoInMemory.retrieveAllListsOfUsername("multiuser");
        assertNotNull(retrievedLists);
        assertEquals(2, retrievedLists.size());
        assertTrue(retrievedLists.stream().anyMatch(l -> l.getId() == 101));
        assertTrue(retrievedLists.stream().anyMatch(l -> l.getId() == 102));
    }

    @Test
    @DisplayName("retrieveById: Should retrieve an existing list by ID")
    void testRetrieveById_ExistingList() throws ExceptionDao {
        UserBean user = new UserBean("testuser", "pass");
        ListBean list = new ListBean(5, "Existing List", "testuser");
        listDaoInMemory.saveList(list, user);

        ListBean retrievedList = listDaoInMemory.retrieveById(5);
        assertNotNull(retrievedList);
        assertEquals(5, retrievedList.getId());
        assertEquals("Existing List", retrievedList.getName());
    }

    @Test
    @DisplayName("retrieveById: Should return null for a non-existent list ID")
    void testRetrieveById_NonExistentList() throws ExceptionDao {
        ListBean retrievedList = listDaoInMemory.retrieveById(999);
        assertNull(retrievedList);
    }

    @Test
    @DisplayName("deleteList: Should successfully delete an existing list")
    void testDeleteList_Success() throws ExceptionDao {
        UserBean user = new UserBean("deluser", "pass");
        ListBean listToDelete = new ListBean(20, "Delete Me", "deluser");
        listDaoInMemory.saveList(listToDelete, user);

        assertNotNull(listDaoInMemory.retrieveById(20));

        listDaoInMemory.deleteList(listToDelete);

        assertNull(listDaoInMemory.retrieveById(20));
        List<ListBean> userLists = listDaoInMemory.retrieveAllListsOfUsername("deluser");
        assertTrue(userLists.isEmpty());
    }

    @Test
    @DisplayName("deleteList: Should throw ExceptionDao if list not found for deletion")
    void testDeleteList_NotFound() {
        ListBean nonExistentList = new ListBean(21, "Not Here", "someuser");
        ExceptionDao thrown = assertThrows(ExceptionDao.class, () -> listDaoInMemory.deleteList(nonExistentList));
        assertTrue(thrown.getMessage().contains("List with ID 21 not found for deletion."));
    }

    @Test
    @DisplayName("deleteList: Should throw IllegalArgumentException if list is null")
    void testDeleteList_NullList() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> listDaoInMemory.deleteList(null));
        assertEquals("List cannot be null.", thrown.getMessage());
    }

    @Test
    @DisplayName("deleteList: Should remove user from map if it was their last list")
    void testDeleteList_LastListForUser() throws ExceptionDao {
        UserBean user = new UserBean("lastlistuser", "pass");
        ListBean list = new ListBean(30, "The Last One", "lastlistuser");
        listDaoInMemory.saveList(list, user);

        assertFalse(listDaoInMemory.retrieveAllListsOfUsername("lastlistuser").isEmpty());

        listDaoInMemory.deleteList(list);

        assertTrue(listDaoInMemory.retrieveAllListsOfUsername("lastlistuser").isEmpty());
    }

    @Test
    @DisplayName("retrieveAllListsOfUsername: Should retrieve all lists for a user")
    void testRetrieveAllListsOfUsername_MultipleLists() throws ExceptionDao {
        UserBean userA = new UserBean("userA", "passA");
        UserBean userB = new UserBean("userB", "passB");

        listDaoInMemory.saveList(new ListBean(10, "A's List 1", "userA"), userA);
        listDaoInMemory.saveList(new ListBean(11, "A's List 2", "userA"), userA);
        listDaoInMemory.saveList(new ListBean(12, "B's List 1", "userB"), userB);

        List<ListBean> userALists = listDaoInMemory.retrieveAllListsOfUsername("userA");
        assertNotNull(userALists);
        assertEquals(2, userALists.size());
        assertTrue(userALists.stream().anyMatch(l -> l.getId() == 10));
        assertTrue(userALists.stream().anyMatch(l -> l.getId() == 11));
    }

    @Test
    @DisplayName("retrieveAllListsOfUsername: Should return an empty list for user with no lists")
    void testRetrieveAllListsOfUsername_NoLists() throws ExceptionDao {
        List<ListBean> emptyLists = listDaoInMemory.retrieveAllListsOfUsername("emptyuser");
        assertNotNull(emptyLists);
        assertTrue(emptyLists.isEmpty());
    }

    @Test
    @DisplayName("retrieveAllListsOfUsername: Should return an empty list for a non-existent username")
    void testRetrieveAllListsOfUsername_NonExistentUser() throws ExceptionDao {
        List<ListBean> lists = listDaoInMemory.retrieveAllListsOfUsername("nonexistent");
        assertNotNull(lists);
        assertTrue(lists.isEmpty());
    }

    @Test
    @DisplayName("retrieveAllListsOfUsername: Should throw IllegalArgumentException if username is null")
    void testRetrieveAllListsOfUsername_NullUsername() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> listDaoInMemory.retrieveAllListsOfUsername(null));
        assertEquals("Username cannot be null.", thrown.getMessage());
    }

    @Test
    @DisplayName("retrieveAllListsOfUsername: Returned list should be unmodifiable")
    void testRetrieveAllListsOfUsername_IsUnmodifiable() throws ExceptionDao {
        UserBean user = new UserBean("unmoduser", "pass");
        listDaoInMemory.saveList(new ListBean(1, "List 1", "unmoduser"), user);

        List<ListBean> lists = listDaoInMemory.retrieveAllListsOfUsername("unmoduser");

        ListBean newListBean = new ListBean(2, "New", "unmoduser");

        assertThrows(UnsupportedOperationException.class, () -> lists.add(newListBean));
    }
}