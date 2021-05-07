package domain;

import domain.models.Drug;
import domain.models.User;
import domain.validation.DrugValidator;
import domain.validation.UserValidator;
import domain.validation.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestValidation {
    @Test
    public void ValidUser_Validate_NoExceptionThrown() {
        var validUser = new User.Builder().build();

        assertDoesNotThrow(() -> new UserValidator().validate(validUser));
    }

    @Test
    public void InvalidUser_Validate_ValidationExceptionThrown() {
        var invalidUser = new User.Builder().withId(-1).build();

        var exception = assertThrows(ValidationException.class, () -> new UserValidator().validate(invalidUser));
        assertEquals("User id can not be negative.", exception.getMessage());
    }

    @Test
    public void UserWithNullProperty_Validate_ValidationExceptionThrown() {
        var invalidUser = new User.Builder().withUserName(null).build();

        var exception = assertThrows(ValidationException.class, () -> new UserValidator().validate(invalidUser));
        assertEquals("Some or all of the properties of the entity were null.", exception.getMessage());
    }

    @Test
    public void ValidDrug_Validate_NoExceptionThrown() {
        var validDrug = new Drug.Builder().build();

        assertDoesNotThrow(() -> new DrugValidator().validate(validDrug));
    }

    @Test
    public void InvalidDrug_Validate_ValidationExceptionThrown() {
        var invalidDrug = new Drug.Builder().withId(-1).build();

        var exception = assertThrows(ValidationException.class, () -> new DrugValidator().validate(invalidDrug));
        assertEquals("Drug id can not be negative.", exception.getMessage());
    }

    @Test
    public void DrugWithNullProperty_Validate_ValidationExceptionThrown() {
        var invalidDrug = new Drug.Builder().withName(null).build();

        var exception = assertThrows(ValidationException.class, () -> new DrugValidator().validate(invalidDrug));
        assertEquals("Some or all of the properties of the entity were null.", exception.getMessage());
    }
}
