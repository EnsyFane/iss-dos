package domain.validation;

import domain.models.User;
import utils.Constants;

import java.util.regex.Pattern;

public class UserValidator extends AbstractValidator<Integer, User> {
    private final String emailRegex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    private final Pattern pattern = Pattern.compile(emailRegex);

    @Override
    protected String validateEntity(User entity) {
        var message = "";

        if (entity.getId() < 0)
        {
            message += "User id can not be negative.\n";
        }
        if (entity.getUserName().length() == 0)
        {
            message += "User name property can not be empty.\n";
        }
        if (entity.getFirstName().length() == 0)
        {
            message += "First name property can not be empty.\n";
        }
        if (entity.getLastName().length() == 0)
        {
            message += "Last name property can not be empty.\n";
        }
        if (entity.getEncryptedPassword().length() != Constants.ENCRYPTED_PASSWORD_LENGTH)
        {
            message += "Encrypted password property must be " + Constants.ENCRYPTED_PASSWORD_LENGTH + " characters long.\n";
        }
        if (entity.getSalt().length() != Constants.SALT_LENGTH)
        {
            message += "Salt property must be " + Constants.SALT_LENGTH + " characters long.\n";
        }
        var matcher = pattern.matcher(entity.getEmail());
        if (!matcher.matches()) {
            message += "Email is invalid.\n";
        }
        return message;
    }
}
