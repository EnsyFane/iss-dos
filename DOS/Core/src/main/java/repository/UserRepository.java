package repository;

import domain.models.User;
import domain.models.UserType;
import domain.validation.IValidator;
import domain.validation.UserValidator;
import domain.validation.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.JdbcUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class UserRepository implements IUserRepository {
    private final JdbcUtils dbUtils;
    private final IValidator<Integer, User> validator;

    private static final Logger _logger = LogManager.getLogger();

    public UserRepository(String jdbcUrl) {
        _logger.info("Initializing User Repository.");

        var props = new Properties();
        props.setProperty("jdbc.url", jdbcUrl);
        dbUtils = new JdbcUtils(props);
        validator = new UserValidator();
    }

    @Override
    public Optional<User> getById(Integer id) {
        _logger.traceEntry("Getting User with ID: {}.", id);

        var con = dbUtils.getConnection();
        try (var statement = con.prepareStatement("SELECT * FROM users WHERE id=?;")) {
            statement.setInt(1, id);
            try (var result = statement.executeQuery()) {
                if (!result.next()) {
                    _logger.warn("No User with ID: {}.", id);

                    return _logger.traceExit(Optional.empty());
                }

                var user = generateUserFromResult(result);

                return _logger.traceExit("Found User: {}.", Optional.of(user));
            }
        } catch (SQLException | IndexOutOfBoundsException ex) {
            _logger.error(ex);

            return _logger.traceExit(Optional.empty());
        }
    }

    @Override
    public List<User> getAll() {
        _logger.traceEntry("Getting all users.");

        var finalList = new ArrayList<User>();
        var con = dbUtils.getConnection();
        try (var statement = con.prepareStatement("SELECT * FROM users;")) {
            try (var result = statement.executeQuery()) {
                while (result.next()) {
                    var user = generateUserFromResult(result);

                    finalList.add(user);
                }
            }
        } catch (SQLException | IndexOutOfBoundsException ex) {
            _logger.error(ex);

            return _logger.traceExit(new ArrayList<>());
        }

        _logger.traceExit("Retrieved {} users.", finalList.size());
        return finalList;
    }

    @Override
    public Optional<User> add(User entity) throws ValidationException, IllegalArgumentException {
        _logger.traceEntry("Adding User to repo.");

        if (entity == null) {
            _logger.error("Null User received.");

            throw _logger.traceExit(new IllegalArgumentException("Null User received."));
        }

        try {
            validator.validate(entity);
        }
        catch (ValidationException ex) {
            _logger.error(ex);

            throw _logger.traceExit(ex);
        }

        var con = dbUtils.getConnection();
        try (var statement = con.prepareStatement("INSERT INTO users(username, firstName, lastName, encryptedPassword, salt, userType, email, nextPasswordChange) VALUES (?,?,?,?,?,?,?,?);")) {
            statement.setString(1, entity.getUserName());
            statement.setString(2, entity.getFirstName());
            statement.setString(3, entity.getLastName());
            statement.setString(4, entity.getEncryptedPassword());
            statement.setString(5, entity.getSalt());
            statement.setInt(6, entity.getUserType().toDatabaseRepresentation());
            statement.setString(7, entity.getEmail());
            statement.setDate(8, entity.getNextPasswordChange());

            var result = statement.executeUpdate();
            if (result == 1) {
                try (var extraStatement = con.prepareStatement("SELECT seq FROM sqlite_sequence WHERE name='users';")) {
                    try(var extraResult = extraStatement.executeQuery()) {
                        extraResult.next();
                        var storedId = extraResult.getInt(1);
                        entity.setId(storedId);
                    }
                }
                _logger.traceExit("User added to repo.");

                return Optional.empty();
            } else {
                _logger.error("User with ID: {} already stored in repo.", entity.getId());

                return _logger.traceExit(getById(entity.getId()));
            }
        } catch (SQLException ex) {
            _logger.error(ex);

            return _logger.traceExit(Optional.empty());
        }
    }

    @Override
    public Optional<User> remove(Integer id) {
        _logger.traceEntry("Removing User with ID: {}.", id);

        var oldEntry = getById(id);
        if (oldEntry.isEmpty()) {
            _logger.warn("No User with ID: {}.", id);

            return _logger.traceExit(Optional.empty());
        }

        var con = dbUtils.getConnection();
        try (var statement = con.prepareStatement("DELETE FROM users WHERE id=?;")) {
            statement.setInt(1, id);

            var result = statement.executeUpdate();
            if (result == 1) {
                _logger.traceExit("User removed.");

                return oldEntry;
            } else {
                _logger.warn("No user with ID: {}.", id);

                return _logger.traceExit(Optional.empty());
            }
        } catch (SQLException ex) {
            _logger.error(ex);

            return _logger.traceExit(Optional.empty());
        }
    }

    @Override
    public Optional<User> update(User entity) throws ValidationException, IllegalArgumentException {
        _logger.traceEntry("Updating User.");

        if (entity == null) {
            _logger.error("Null User received.");

            throw _logger.traceExit(new IllegalArgumentException("Null User received."));
        }

        try {
            validator.validate(entity);
        }
        catch (ValidationException ex) {
            _logger.error(ex);

            throw _logger.traceExit(ex);
        }

        var oldMatch = getById(entity.getId());

        if (oldMatch.isEmpty()) {
            _logger.traceExit("No User with ID: {}.", entity.getId());

            return _logger.traceExit(Optional.empty());
        }

        var con = dbUtils.getConnection();
        try (var statement = con.prepareStatement("UPDATE users SET username=?, firstName=?, lastName=?, encryptedPassword=?, salt=?, userType=?, email=?,nextPasswordChange=? WHERE id=?;")) {
            statement.setString(1, entity.getUserName());
            statement.setString(2, entity.getFirstName());
            statement.setString(3, entity.getLastName());
            statement.setString(4, entity.getEncryptedPassword());
            statement.setString(5, entity.getSalt());
            statement.setInt(6, entity.getUserType().toDatabaseRepresentation());
            statement.setString(7, entity.getEmail());
            statement.setDate(8, entity.getNextPasswordChange());
            statement.setInt(9, entity.getId());

            var result = statement.executeUpdate();
            if (result == 1) {
                _logger.traceExit("User with ID {} updated.", entity.getId());

                return oldMatch;
            } else {
                _logger.error("No User with ID: {}.", entity.getId());

                return _logger.traceExit(Optional.empty());
            }
        } catch (SQLException ex) {
            _logger.error(ex);

            return _logger.traceExit(Optional.empty());
        }
    }

    @Override
    public void clear() {
        _logger.traceEntry("Clearing User repo.");

        var con = dbUtils.getConnection();

        //noinspection SqlWithoutWhere
        try (var statement = con.prepareStatement("DELETE FROM users;")) {
            statement.executeUpdate();
        } catch (SQLException ex) {
            _logger.error(ex);
        }

        try (var statement = con.prepareStatement("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='users';")) {
            statement.executeUpdate();
        } catch (SQLException ex) {
            _logger.error(ex);
        }

        _logger.traceExit("User repo cleared.");
    }

    @Override
    public Optional<User> getByUsername(String username) {
        _logger.traceEntry("Getting User with user name: {}.", username);

        var con = dbUtils.getConnection();
        try (var statement = con.prepareStatement("SELECT * FROM users WHERE username=?;")) {
            statement.setString(1, username);
            try (var result = statement.executeQuery()) {
                if (!result.next()) {
                    _logger.warn("No User with user name: {}.", username);

                    return _logger.traceExit(Optional.empty());
                }

                var user = generateUserFromResult(result);

                return _logger.traceExit("Found User: {}.", Optional.of(user));
            }
        } catch (SQLException ex) {
            _logger.error(ex);

            return _logger.traceExit(Optional.empty());
        }
    }

    private User generateUserFromResult(ResultSet result) throws SQLException, IndexOutOfBoundsException {
        _logger.info("Generating User from result.");

        var resultId = result.getInt(1);
        var resultUsername = result.getString(2);
        var resultFirstName = result.getString(3);
        var resultLastName = result.getString(4);
        var resultEncryptedPassword = result.getString(5);
        var resultSalt = result.getString(6);
        var resultUserType = UserType.fromDatabaseRepresentation(result.getInt(7));
        var resultEmail = result.getString(8);
        var resultNextPasswordChange = result.getDate(9);

        var user = new User();
        user.setId(resultId);
        user.setUserName(resultUsername);
        user.setFirstName(resultFirstName);
        user.setLastName(resultLastName);
        user.setEncryptedPassword(resultEncryptedPassword);
        user.setSalt(resultSalt);
        user.setUserType(resultUserType);
        user.setEmail(resultEmail);
        user.setNextPasswordChange(resultNextPasswordChange);

        return user;
    }
}
