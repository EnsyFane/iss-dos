package repository;

import domain.models.User;

import java.util.Optional;

public interface IUserRepository extends IRepository<Integer, User> {
    /**
     * Retrieves a list of {@link User}s with the given {@code userName}.
     * @param username the {@code userName} of the {@link User}.
     * @return a list of {@link User}s.
     */
    Optional<User> getByUsername(String username);
}
