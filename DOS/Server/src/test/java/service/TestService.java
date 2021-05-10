package service;

import domain.dto.UserDTO;
import domain.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import repository.*;
import utils.Constants;
import utils.PasswordUtils;
import utils.TestConstants;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class TestService {
    private static IUserRepository _userRepo;
    private static IDrugRepository _drugRepo;
    private static IOrderRepository _orderRepo;
    private static IDOSService _service;

    @BeforeAll
    public static void SetupService() {
        var props = new Properties();
        try {
            props.load(new FileReader(TestConstants.TEST_JAVA_DIRECTORY + '/' + TestConstants.TEST_CONFIG_FILE));
        } catch (IOException e) {
            System.out.println("Cannot find " + TestConstants.TEST_CONFIG_FILE + ".\n");
            e.printStackTrace();
            return;
        }

        _userRepo = new UserRepository(props.getProperty("jdbc.url"));
        _drugRepo = new DrugRepository(props.getProperty("jdbc.url"));
        _orderRepo = new OrderRepository(props.getProperty("jdbc.url"));
        _userRepo.clear();
        _drugRepo.clear();
        _orderRepo.clear();

        _service = new DOSService(_userRepo, _drugRepo, _orderRepo);
    }

    @AfterEach
    public void ClearDBs() {
        _userRepo.clear();
        _drugRepo.clear();
    }

    @Test
    public void CorrectLoginInfo_LoginUser_UserLoggedIn() {
        var user = new User.Builder()
                .withUserName("test-user")
                .withPassword("password")
                .build();
        _service.addUser(user);
        var details = new UserDTO(user.getUserName(), "password");

        var result = _service.loginUser(details, null);

        assertEquals(user, result);
    }

    @Test
    public void InvalidLoginInfo_LoginUser_UserNotLoggedIn() {
        var user = new User.Builder()
                .withUserName("test-user")
                .withPassword("password")
                .build();
        _service.addUser(user);
        var details = new UserDTO(user.getUserName(), "bad-password");

        var result = _service.loginUser(details, null);

        assertNull(result);
    }

    @Test
    public void AddUser_UserIsAdded() {
        var user = new User.Builder().withEncryptedPassword("pwd").withSalt("").build();

        var result = _service.addUser(user);

        assertNull(result);
        assertNotEquals(0, user.getSalt().length());
        assertNotEquals("pwd", user.getEncryptedPassword());
    }

    @Test
    public void AddUserWithEncryptedPassword_UserIsAdded() {
        var user = new User.Builder().withPassword("pwd").build();

        var result = _service.addUser(user);

        assertNull(result);
        assertEquals(PasswordUtils.encryptPassword("pwd", user.getSalt()), user.getEncryptedPassword());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void UserInApp_ChangePassword_PasswordIsChanged() {
        var date = new Date(System.currentTimeMillis());
        var expectedDate = new Date(date.getTime() + Constants.DISTANCE_BETWEEN_PASSWORD_CHANGES);
        var user = new User.Builder()
                .withPassword("old-pwd")
                .withNextPasswordChange(date)
                .build();
        _service.addUser(user);

        var result = _service.changePassword(user.getId(), "old-pwd", "new-pwd");
        var updatedUser = _userRepo.getById(user.getId());

        assertTrue(result);
        assertTrue(updatedUser.isPresent());
        assertEquals(PasswordUtils.encryptPassword("new-pwd", updatedUser.get().getSalt()), updatedUser.get().getEncryptedPassword());
        assertEquals(expectedDate.getYear(), updatedUser.get().getNextPasswordChange().getYear());
        assertEquals(expectedDate.getMonth(), updatedUser.get().getNextPasswordChange().getMonth());
        assertEquals(expectedDate.getDay(), updatedUser.get().getNextPasswordChange().getDay());
    }
}
