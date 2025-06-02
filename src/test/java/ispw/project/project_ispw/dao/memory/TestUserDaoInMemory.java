package ispw.project.project_ispw.dao.memory;

import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.exception.ExceptionDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class TestUserDaoInMemory {

    private UserDaoInMemory userDaoInMemory;

    @BeforeEach
    void setUp() {
        userDaoInMemory = new UserDaoInMemory();
    }

    @Test
    @DisplayName("saveUser: Should successfully save a new user")
    void testSaveUser_Success() throws ExceptionDao {
        UserBean user = new UserBean("newuser", "pass123");
        userDaoInMemory.saveUser(user);

        UserBean retrievedUser = userDaoInMemory.retrieveByUsername("newuser");
        assertNotNull(retrievedUser);
        assertEquals("newuser", retrievedUser.getUsername());
        assertEquals("pass123", retrievedUser.getPassword());
    }

    @Test
    @DisplayName("saveUser: Should throw ExceptionDao if username already exists")
    void testSaveUser_UsernameExists() throws ExceptionDao {
        UserBean existingUser = new UserBean("existinguser", "pass123");
        userDaoInMemory.saveUser(existingUser);

        UserBean duplicateUser = new UserBean("existinguser", "newpass");

        ExceptionDao thrown = assertThrows(ExceptionDao.class, () -> userDaoInMemory.saveUser(duplicateUser));

        assertTrue(thrown.getMessage().contains("Username already exists: existinguser"));
    }

    @Test
    @DisplayName("saveUser: Should throw IllegalArgumentException if user is null")
    void testSaveUser_NullUser() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> userDaoInMemory.saveUser(null));

        assertEquals("User cannot be null.", thrown.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    @DisplayName("saveUser: Should throw IllegalArgumentException for null, empty, or blank usernames")
    void testSaveUser_InvalidUsernames(String username) {
        UserBean user = new UserBean(username, "pass");

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> userDaoInMemory.saveUser(user));

        assertEquals("User's username cannot be null or empty.", thrown.getMessage());
    }

    @Test
    @DisplayName("retrieveByUsername: Should retrieve an existing user")
    void testRetrieveByUsername_ExistingUser() throws ExceptionDao {
        UserBean user = new UserBean("retrieveTestUser", "testpass");
        userDaoInMemory.saveUser(user);

        UserBean retrievedUser = userDaoInMemory.retrieveByUsername("retrieveTestUser");

        assertNotNull(retrievedUser);
        assertEquals("retrieveTestUser", retrievedUser.getUsername());
        assertEquals("testpass", retrievedUser.getPassword());
    }

    @Test
    @DisplayName("retrieveByUsername: Should return null for a non-existent user")
    void testRetrieveByUsername_NonExistentUser() throws ExceptionDao {
        UserBean retrievedUser = userDaoInMemory.retrieveByUsername("nonexistentUser");

        assertNull(retrievedUser);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    @DisplayName("retrieveByUsername: Should throw IllegalArgumentException for null, empty, or blank usernames")
    void testRetrieveByUsername_InvalidUsernames(String username) {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> userDaoInMemory.retrieveByUsername(username));

        assertEquals("Username cannot be null or empty.", thrown.getMessage());
    }
}