package domain.validation;

import domain.models.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base class for all {@link Entity} {@code Validators}.
 * @param <ID> the type of the ID of the object to validate.
 * @param <T> the type of the object to validate.
 */
public abstract class AbstractValidator<ID, T extends Entity<ID>> implements IValidator<ID, T> {
    private static final Logger _logger = LogManager.getLogger();

    @Override
    public void validate(T entity) throws ValidationException {
        try {
            for (var f : entity.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                if (f.get(entity) == null) {
                    _logger.warn("Some or all of the properties of the entity were null.");

                    throw new ValidationException("Some or all of the properties of the entity were null.");
                }
            }
        }
        catch (IllegalAccessException e) {
            _logger.error("Some or all of the properties of the entity could not be accessed.");

            throw new ValidationException("Some or all of the properties of the entity could not be accessed.");
        }

        finalizeValidation((validateEntity(entity)));
    }

    protected abstract String validateEntity(T entity);

    /**
     * Finalizes the validation and throws a {@link ValidationException} if the validated {@link Entity} is invalid.
     * @param message the message of the {@link ValidationException}.
     * @throws ValidationException if the validated {@link Entity} is invalid.
     */
    private void finalizeValidation(String message) throws ValidationException {
        if (!message.isEmpty()) {
            message = message.substring(0, message.length() - 1);

            _logger.warn("Validation failed: {}", message);

            throw new ValidationException(message);
        }
    }
}