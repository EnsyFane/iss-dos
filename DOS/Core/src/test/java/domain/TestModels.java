package domain;

import domain.models.Drug;
import domain.models.User;
import domain.models.UserType;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestModels {
    @Test
    public void User_Constructor() {
        var date = new Date(System.currentTimeMillis());

        var user  = new User(1, "user-name", "first-name", "last-name", "encrypted-password", "salt", UserType.Admin, "email", date);

        assertEquals(1, user.getId());
        assertEquals("user-name", user.getUserName());
        assertEquals("first-name", user.getFirstName());
        assertEquals("last-name", user.getLastName());
        assertEquals("encrypted-password", user.getEncryptedPassword());
        assertEquals("salt", user.getSalt());
        assertEquals(UserType.Admin, user.getUserType());
        assertEquals("email", user.getEmail());
        assertEquals(date, user.getNextPasswordChange());
    }

    @Test
    public void Drug_Constructor() {
        var drug = new Drug(1, "name", "description", 26);

        assertEquals(1, drug.getId());
        assertEquals("name", drug.getName());
        assertEquals("description", drug.getDescription());
        assertEquals(26, drug.getInStock());
    }
}
