package repository;

import domain.models.User;
import domain.validation.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import test.utils.TestConstants;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class TestUserRepository {
    private static IUserRepository _userRepo;

    @BeforeAll
    public static void SetupDB() {
        var props = new Properties();
        try {
            props.load(new FileReader(TestConstants.TEST_JAVA_DIRECTORY + '/' + TestConstants.TEST_CONFIG_FILE));
        } catch (IOException e) {
            System.out.println("Cannot find " + TestConstants.TEST_CONFIG_FILE + ".\n");
            e.printStackTrace();
            return;
        }

        _userRepo = new UserRepository(props.getProperty("jdbc.url"));
        _userRepo.clear();
    }

    @AfterEach
    public void ClearDB() {
        _userRepo.clear();
    }

    @Test
    public void UserRepo_GetByIdWithNotStoredId_ReturnsEmptyOptional() {
        var storedInRepo = addUserToRepo();

        var response = _userRepo.getById(storedInRepo.getId() + 1);

        assertTrue(response.isEmpty());
    }

    @Test
    public void UserRepo_GetById_ReturnsCorrectUser() {
        var storedInRepo = addUserToRepo();

        var response = _userRepo.getById(storedInRepo.getId());

        assertTrue(response.isPresent());
        assertEquals(storedInRepo, response.get());
    }

    @Test
    public void UserRepoWithMultipleUsers_GetById_ReturnsCorrectUser() {
        var users = addUsersToRepo(4);

        var actualUser0 = _userRepo.getById(users.get(0).getId());
        var actualUser1 = _userRepo.getById(users.get(1).getId());

        assertTrue(actualUser0.isPresent());
        assertTrue(actualUser1.isPresent());
        assertEquals(users.get(0), actualUser0.get());
        assertEquals(users.get(1), actualUser1.get());
    }

    @Test
    public void EmptyUserRepo_AddUser_UserIsAdded() {
        var user = new User.Builder().build();

        var response = _userRepo.add(user);
        var allUsers = _userRepo.getAll();

        assertTrue(response.isEmpty());
        assertEquals(1, allUsers.size());
        assertEquals(user, allUsers.get(0));
    }

    @Test
    public void UserRepo_AddInvalidUser_ValidationExceptionIsThrown() {
        var user = new User.Builder().withUserName(null).build();

        var exception = assertThrows(ValidationException.class, () -> _userRepo.add(user));
        assertEquals("Some or all of the properties of the entity were null.", exception.getMessage());
    }

    @Test
    public void UserRepo_AddNullUser_IllegalArgumentExceptionThrown() {
        var exception = assertThrows(IllegalArgumentException.class, () -> _userRepo.add(null));
        assertEquals("Null User received.", exception.getMessage());
    }

    @Test
    public void UserRepoWithMultipleUsers_GetAll_ReturnsAllStoredUsers() {
        var users = addUsersToRepo(50);

        var actualUsers = _userRepo.getAll();

        assertEquals(50, actualUsers.size());
        assertEquals(users, actualUsers);
    }

    @Test
    public void UserRepoWithUser_Remove_UserIsDeleted() {
        var user = addUserToRepo();

        var deleted = _userRepo.remove(user.getId());
        var inDatabase = _userRepo.getById(user.getId());

        assertTrue(inDatabase.isEmpty());
        assertTrue(deleted.isPresent());
        assertEquals(user, deleted.get());
    }

    @Test
    public void UserRepo_UpdateUser_UserIsUpdated() {
        var user = addUserToRepo();
        var updatedUser = new User.Builder().from(user).withUserName("my-name").build();

        var oldUser = _userRepo.update(updatedUser);
        var inDatabase = _userRepo.getById(updatedUser.getId());

        assertTrue(oldUser.isPresent());
        assertTrue(inDatabase.isPresent());
        assertEquals(user, oldUser.get());
        assertEquals(updatedUser, inDatabase.get());
    }

    @Test
    public void UserRepo_UpdateWithInvalidUser_ValidationExceptionIsThrown() {
        addUsersToRepo(2);
        var user = new User.Builder().withUserName(null).build();

        var exception = assertThrows(ValidationException.class, () -> _userRepo.update(user));
        assertEquals("Some or all of the properties of the entity were null.", exception.getMessage());
    }

    @Test
    public void UserRepo_UpdateWithNullUser_IllegalArgumentExceptionThrown() {
        addUsersToRepo(2);

        var exception = assertThrows(IllegalArgumentException.class, () -> _userRepo.update(null));
        assertEquals("Null User received.", exception.getMessage());
    }

    @Test
    public void UserRepo_GetByNonexistentUserName_ReturnsEmpty() {
        addUserToRepo();

        var found = _userRepo.getByUsername("my-random-user-name");

        assertTrue(found.isEmpty());
    }

    @Test
    public void UserRepo_GetByUserName_ReturnsCorrectUser() {
        addUserToRepo();
        var user = new User.Builder().withUserName("Stefan").build();
        _userRepo.add(user);

        var found = _userRepo.getByUsername(user.getUserName());

        assertTrue(found.isPresent());
        assertEquals(user, found.get());
    }

    @Test
    public void UserRepo_GetByNullUserName_ReturnsEmpty() {
        var found = _userRepo.getByUsername(null);

        assertTrue(found.isEmpty());
    }

    private User addUserToRepo() {
        var user = new User.Builder().build();
        _userRepo.add(user);
        return user;
    }

    private List<User> addUsersToRepo(int amount) {
        var users = new ArrayList<User>();
        for (var i = 0; i < amount; i++) {
            var user = new User.Builder().withUserName("user-" + i).build();
            _userRepo.add(user);
            users.add(user);
        }

        return users;
    }
}
