package utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for PasswordUtils class.
 */
public class TestPasswordUtils {
    @Test
    public void EncryptPassword_ReturnsSameEncryptionEveryTime()
    {
        var password = "password";
        var salt = "salt";

        var encrypted1 = PasswordUtils.encryptPassword(password, salt);
        var encrypted2 = PasswordUtils.encryptPassword(password, salt);

        assertEquals(encrypted1, encrypted2);
    }

    @Test
    public void GenerateSalt_ReturnsValidSalt()
    {
        var validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        var generatedSalt = PasswordUtils.generateSalt(Constants.SALT_LENGTH);

        assertEquals(Constants.SALT_LENGTH, generatedSalt.length());
        for (var character : generatedSalt.toCharArray()) {
            assertTrue(validChars.contains("" + character));
        }
    }
}