package repository;

import domain.models.Entity;
import domain.validation.ValidationException;

import java.util.List;
import java.util.Optional;

/**
 * Interface for a {@code Repository}.
 * @param <ID> the type of the ID of the stored object.
 * @param <T> the type of the stored object.
 */
public interface IRepository<ID, T extends Entity<ID>> {
    /**
     * Retrieves an {@link Entity} from the database.
     * @param id the id of the {@link Entity} to retrieve.
     * @return - an empty {@link Optional} if no {@link Entity} with the given {@code ID} is stored in the database.
     *         - an {@link Optional} with the requested {@link Entity}.
     */
    Optional<T> getById(ID id);

    /**
     * Retrieves a {@link List} of all {@link Entity} objects stored in the database.
     * @return a {@link List} of all {@link Entity} objects stored in the database.
     */
    List<T> getAll();

    /**
     * Adds an {@link Entity} to the database.
     * @param entity the {@link Entity} to be added.
     * @return - an empty {@link Optional} if the {@link Entity} was stored successfully.
     *         - an {@link Optional} with a stored {@link Entity} if an {@link Entity} with the given {@code ID} is already stored in the database.
     * @throws ValidationException if the given {@link Entity} is invalid.
     * @throws IllegalArgumentException if the given {@link Entity} is {@code null}.
     */
    Optional<T> add(T entity) throws ValidationException, IllegalArgumentException;

    /**
     * Removes an {@link Entity} from the database.
     * @param id the {@code ID} of the {@link Entity} to be removed.
     * @return - an empty {@link Optional} if a {@link Entity} with the given {@code ID} doesn't exist in the database.
     *         - an {@link Optional} with the removed {@link Entity} if the {@link Entity} was removed successfully.
     */
    Optional<T> remove(ID id);

    /**
     * Updates an {@link Entity} from the database.
     * @param entity the updated {@link Entity}.
     * @return - an empty {@link Optional} if the {@link Entity} could not be updated.
     *         - an {@link Optional} with the old {@link Entity} if the {@link Entity} was updated successfully.
     * @throws ValidationException if the given {@link Entity} is invalid.
     * @throws IllegalArgumentException if the give {@link Entity} is {@code null}.
     */
    Optional<T> update(T entity) throws ValidationException, IllegalArgumentException;

    /**
     * Clears the database of all entries.
     */
    void clear();
}
