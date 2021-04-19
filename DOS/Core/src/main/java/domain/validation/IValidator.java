package domain.validation;

import domain.models.Entity;

/**
 * Interface for a {@code Validator} for {@link Entity} objects.
 * @param <ID> the type of the ID of the object to validate.
 * @param <T> the type of the object to validate.
 */
public interface IValidator<ID, T extends Entity<ID>> {
    /**
     * Validates an {@link Entity} of type {@link T}.
     * @param entity the {@link Entity} to validate.
     * @throws ValidationException if the given {@link Entity} is invalid.
     */
    void validate(T entity) throws ValidationException;
}