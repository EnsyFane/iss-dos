package domain;

import domain.dto.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDTO {
    @Test
    public void UserDTO_Constructor() {
        var dto = new UserDTO("user-name", "password");

        assertEquals("user-name", dto.getUserName());
        assertEquals("password", dto.getPassword());
    }

    @Test
    public void UserDTO_Setters() {
        var dto = new UserDTO("user-name", "password");

        dto.setUserName("new-user-name");
        dto.setPassword("new-password");

        assertEquals("new-user-name", dto.getUserName());
        assertEquals("new-password", dto.getPassword());
    }
}
