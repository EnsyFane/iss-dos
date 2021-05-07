package repository;

import domain.models.Drug;
import domain.validation.DrugValidator;
import domain.validation.IValidator;
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

public class DrugRepository implements IDrugRepository {
    private final JdbcUtils dbUtils;
    private final IValidator<Integer, Drug> validator;

    private static final Logger _logger = LogManager.getLogger();

    public DrugRepository(String jdbcUrl) {
        _logger.info("Initializing Drug Repository.");

        var props = new Properties();
        props.setProperty("jdbc.url", jdbcUrl);
        dbUtils = new JdbcUtils(props);
        validator = new DrugValidator();
    }

    @Override
    public List<Drug> getAvailableDrugs() {
        _logger.traceEntry("Getting all drugs.");

        var finalList = new ArrayList<Drug>();
        var con = dbUtils.getConnection();
        try (var statement = con.prepareStatement("SELECT * FROM drugs WHERE in_stock > 0;")) {
            try (var result = statement.executeQuery()) {
                while (result.next()) {
                    var drug = generateDrugFromResult(result);

                    finalList.add(drug);
                }
            }
        } catch (SQLException | IndexOutOfBoundsException ex) {
            _logger.error(ex);

            return _logger.traceExit(new ArrayList<>());
        }

        _logger.traceExit("Retrieved {} drugs.", finalList.size());
        return finalList;
    }

    @Override
    public Optional<Drug> getById(Integer id) {
        _logger.traceEntry("Getting Drug with ID: {}.", id);

        var con = dbUtils.getConnection();
        try (var statement = con.prepareStatement("SELECT * FROM drugs WHERE id=?;")) {
            statement.setInt(1, id);
            try (var result = statement.executeQuery()) {
                if (!result.next()) {
                    _logger.warn("No Drug with ID: {}.", id);

                    return _logger.traceExit(Optional.empty());
                }

                var drug = generateDrugFromResult(result);

                return _logger.traceExit("Found Drug: {}.", Optional.of(drug));
            }
        } catch (SQLException | IndexOutOfBoundsException ex) {
            _logger.error(ex);

            return _logger.traceExit(Optional.empty());
        }
    }

    @Override
    public List<Drug> getAll() {
        _logger.traceEntry("Getting all drugs.");

        var finalList = new ArrayList<Drug>();
        var con = dbUtils.getConnection();
        try (var statement = con.prepareStatement("SELECT * FROM drugs;")) {
            try (var result = statement.executeQuery()) {
                while (result.next()) {
                    var drug = generateDrugFromResult(result);

                    finalList.add(drug);
                }
            }
        } catch (SQLException | IndexOutOfBoundsException ex) {
            _logger.error(ex);

            return _logger.traceExit(new ArrayList<>());
        }

        _logger.traceExit("Retrieved {} drugs.", finalList.size());
        return finalList;
    }

    @Override
    public Optional<Drug> add(Drug entity) throws ValidationException, IllegalArgumentException {
        _logger.traceEntry("Adding Drug to repo.");

        if (entity == null) {
            _logger.error("Null Drug received.");

            throw _logger.traceExit(new IllegalArgumentException("Null Drug received."));
        }

        try {
            validator.validate(entity);
        }
        catch (ValidationException ex) {
            _logger.error(ex);

            throw _logger.traceExit(ex);
        }

        var con = dbUtils.getConnection();
        try (var statement = con.prepareStatement("INSERT INTO drugs(name, description, in_stock) VALUES (?,?,?);")) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getDescription());
            statement.setInt(3, entity.getInStock());

            var result = statement.executeUpdate();
            if (result == 1) {
                try (var extraStatement = con.prepareStatement("SELECT seq FROM sqlite_sequence WHERE name='drugs';")) {
                    try(var extraResult = extraStatement.executeQuery()) {
                        extraResult.next();
                        var storedId = extraResult.getInt(1);
                        entity.setId(storedId);
                    }
                }
                _logger.traceExit("Drug added to repo.");

                return Optional.empty();
            } else {
                _logger.error("Drug with ID: {} already stored in repo.", entity.getId());

                return _logger.traceExit(getById(entity.getId()));
            }
        } catch (SQLException ex) {
            _logger.error(ex);

            return _logger.traceExit(Optional.empty());
        }
    }

    @Override
    public Optional<Drug> remove(Integer id) {
        _logger.traceEntry("Removing Drug with ID: {}.", id);

        var oldEntry = getById(id);
        if (oldEntry.isEmpty()) {
            _logger.warn("No Drug with ID: {}.", id);

            return _logger.traceExit(Optional.empty());
        }

        var con = dbUtils.getConnection();
        try (var statement = con.prepareStatement("DELETE FROM drugs WHERE id=?;")) {
            statement.setInt(1, id);

            var result = statement.executeUpdate();
            if (result == 1) {
                _logger.traceExit("Drug removed.");

                return oldEntry;
            } else {
                _logger.warn("No drug with ID: {}.", id);

                return _logger.traceExit(Optional.empty());
            }
        } catch (SQLException ex) {
            _logger.error(ex);

            return _logger.traceExit(Optional.empty());
        }
    }

    @Override
    public Optional<Drug> update(Drug entity) throws ValidationException, IllegalArgumentException {
        _logger.traceEntry("Updating Drug.");

        if (entity == null) {
            _logger.error("Null Drug received.");

            throw _logger.traceExit(new IllegalArgumentException("Null Drug received."));
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
            _logger.traceExit("No Drug with ID: {}.", entity.getId());

            return _logger.traceExit(Optional.empty());
        }

        var con = dbUtils.getConnection();
        try (var statement = con.prepareStatement("UPDATE drugs SET name=?, description=?, in_stock=? WHERE id=?;")) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getDescription());
            statement.setInt(3, entity.getInStock());
            statement.setInt(4, entity.getId());

            var result = statement.executeUpdate();
            if (result == 1) {
                _logger.traceExit("Drug with ID {} updated.", entity.getId());

                return oldMatch;
            } else {
                _logger.error("No Drug with ID: {}.", entity.getId());

                return _logger.traceExit(Optional.empty());
            }
        } catch (SQLException ex) {
            _logger.error(ex);

            return _logger.traceExit(Optional.empty());
        }
    }

    @Override
    public void clear() {
        _logger.traceEntry("Clearing Drug repo.");

        var con = dbUtils.getConnection();

        //noinspection SqlWithoutWhere
        try (var statement = con.prepareStatement("DELETE FROM drugs;")) {
            statement.executeUpdate();
        } catch (SQLException ex) {
            _logger.error(ex);
        }

        try (var statement = con.prepareStatement("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='drugs';")) {
            statement.executeUpdate();
        } catch (SQLException ex) {
            _logger.error(ex);
        }

        _logger.traceExit("Drugs repo cleared.");
    }

    private Drug generateDrugFromResult(ResultSet result) throws SQLException, IndexOutOfBoundsException {
        _logger.info("Generating Drug from result.");

        var resultId = result.getInt(1);
        var resultName = result.getString(2);
        var resultDescription = result.getString(3);
        var resultInStock = result.getInt(4);

        var drug = new Drug();
        drug.setId(resultId);
        drug.setName(resultName);
        drug.setDescription(resultDescription);
        drug.setInStock(resultInStock);

        return drug;
    }
}
