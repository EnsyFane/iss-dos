package utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.SecureRandom;

/**
 * Utility class for password related operations.
 */
public class PasswordUtils {
    private static final Logger _logger = LogManager.getLogger();

    /**
     * Encrypts a given password with a given salt.
     * @param password the plain-text password.
     * @param salt the salt to be added to the password.
     * @return the encrypted password.
     */
    public static String encryptPassword(String password, String salt) {
        _logger.info("Encrypting password with salt: {}", salt);

        return DigestUtils.sha256Hex(password + salt);
    }

    /**
     * Generates a salt with the given length.
     * @param length the length of the salt.
     * @return the generated salt.
     */
    public static String generateSalt(int length) {
        _logger.info("Generating salt of length: {}", length);

        var leftLimit = 48; // numeral '0'
        var rightLimit = 122; // letter 'z'
        var random = new SecureRandom();

        var result = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        _logger.info("Generated salt: {}", result);

        return result;
    }
}