package repository;

import domain.models.Order;
import domain.validation.IValidator;
import domain.validation.OrderValidator;
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

public class OrderRepository implements IOrderRepository {
    private final JdbcUtils dbUtils;
    private final IValidator<Integer, Order> validator;

    private static final Logger _logger = LogManager.getLogger();

    public OrderRepository(String jdbcUrl) {
        _logger.info("Initializing Order Repository.");

        var props = new Properties();
        props.setProperty("jdbc.url", jdbcUrl);
        dbUtils = new JdbcUtils(props);
        validator = new OrderValidator();
    }

    @Override
    public Optional<Order> placeOrder(Order order) {
        _logger.traceEntry("Placing order.");

        var result = add(order);

        if (result.isEmpty()) {
            for (var details : order.getDrugs().keySet()) {
                addOrderDetails(order.getId(), details, order.getDrugs().get(details));
            }
            _logger.traceExit("Order placed.");
        } else {
            _logger.traceExit("Order not placed.");
        }

        return result;
    }

    private void addOrderDetails(Integer orderId, Integer drugId, Integer quality) {
        var con = dbUtils.getConnection();
        try (var statement = con.prepareStatement("INSERT INTO order_details(order_id, drug_id, quantity) VALUES (?,?,?);")) {
            statement.setInt(1, orderId);
            statement.setInt(2, drugId);
            statement.setInt(3, quality);

            var result = statement.executeUpdate();
            if (result != 1) {
                _logger.error("Failed to add order details.");
            }
        } catch (SQLException ex) {
            _logger.error(ex);
        }
    }

    @Override
    public Optional<Order> getById(Integer id) {
        _logger.traceEntry("Getting Order with ID: {}.", id);

        var con = dbUtils.getConnection();
        try (var statement = con.prepareStatement("SELECT * FROM orders WHERE id=?;")) {
            statement.setInt(1, id);
            try (var result = statement.executeQuery()) {
                if (!result.next()) {
                    _logger.warn("No Order with ID: {}.", id);

                    return _logger.traceExit(Optional.empty());
                }

                var orderId = result.getInt(1);
                Order order;

                try (var orderDetailsStatement = con.prepareStatement("SELECT * FROM order_details WHERE order_id=?;")) {
                    orderDetailsStatement.setInt(1, orderId);
                    try (var orderDetails = orderDetailsStatement.executeQuery()) {
                        order = generateOrderFromResult(result, orderDetails);
                    }
                }

                return _logger.traceExit("Found Order: {}.", Optional.of(order));
            }
        } catch (SQLException | IndexOutOfBoundsException ex) {
            _logger.error(ex);

            return _logger.traceExit(Optional.empty());
        }
    }

    @Override
    public List<Order> getAll() {
        _logger.traceEntry("Getting all orders.");

        var finalList = new ArrayList<Order>();
        var con = dbUtils.getConnection();
        try (var statement = con.prepareStatement("SELECT * FROM orders;")) {
            try (var result = statement.executeQuery()) {
                while (result.next()) {
                    var orderId = result.getInt(1);
                    Order order;

                    try (var orderDetailsStatement = con.prepareStatement("SELECT * FROM order_details WHERE order_id=?;")) {
                        orderDetailsStatement.setInt(1, orderId);
                        try (var orderDetails = orderDetailsStatement.executeQuery()) {
                            order = generateOrderFromResult(result, orderDetails);
                        }
                    }

                    finalList.add(order);
                }
            }
        } catch (SQLException | IndexOutOfBoundsException ex) {
            _logger.error(ex);

            return _logger.traceExit(new ArrayList<>());
        }

        _logger.traceExit("Retrieved {} orders.", finalList.size());
        return finalList;
    }

    @Override
    public Optional<Order> add(Order entity) throws ValidationException, IllegalArgumentException {
        _logger.traceEntry("Adding Order to repo.");

        if (entity == null) {
            _logger.error("Null Order received.");

            throw _logger.traceExit(new IllegalArgumentException("Null Order received."));
        }

        try {
            validator.validate(entity);
        }
        catch (ValidationException ex) {
            _logger.error(ex);

            throw _logger.traceExit(ex);
        }

        var con = dbUtils.getConnection();
        try (var statement = con.prepareStatement("INSERT INTO orders(ordered_by, delivered, ordered_at, delivered_At) VALUES (?,?,?,?);")) {
            statement.setInt(1, entity.getOrderedBy());
            statement.setBoolean(2, entity.getDelivered());
            statement.setDate(3, entity.getOrderedAt());
            statement.setDate(4, entity.getDeliveredAt());

            var result = statement.executeUpdate();
            if (result == 1) {
                try (var extraStatement = con.prepareStatement("SELECT seq FROM sqlite_sequence WHERE name='orders';")) {
                    try(var extraResult = extraStatement.executeQuery()) {
                        extraResult.next();
                        var storedId = extraResult.getInt(1);
                        entity.setId(storedId);
                    }
                }
                _logger.traceExit("Order added to repo.");

                return Optional.empty();
            } else {
                _logger.error("Order with ID: {} already stored in repo.", entity.getId());

                return _logger.traceExit(getById(entity.getId()));
            }
        } catch (SQLException ex) {
            _logger.error(ex);

            return _logger.traceExit(Optional.empty());
        }
    }

    @Override
    public Optional<Order> remove(Integer id) {
        _logger.traceEntry("Removing Order with ID: {}.", id);

        var oldEntry = getById(id);
        if (oldEntry.isEmpty()) {
            _logger.warn("No Order with ID: {}.", id);

            return _logger.traceExit(Optional.empty());
        }

        var con = dbUtils.getConnection();
        try (var statement = con.prepareStatement("DELETE FROM orders WHERE id=?;")) {
            statement.setInt(1, id);

            var result = statement.executeUpdate();
            if (result == 1) {
                _logger.traceExit("Order removed.");

                return oldEntry;
            } else {
                _logger.warn("No order with ID: {}.", id);

                return _logger.traceExit(Optional.empty());
            }
        } catch (SQLException ex) {
            _logger.error(ex);

            return _logger.traceExit(Optional.empty());
        }
    }

    @Override
    public Optional<Order> update(Order entity) throws ValidationException, IllegalArgumentException {
        _logger.traceEntry("Updating Order.");

        if (entity == null) {
            _logger.error("Null Order received.");

            throw _logger.traceExit(new IllegalArgumentException("Null Order received."));
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
            _logger.traceExit("No Order with ID: {}.", entity.getId());

            return _logger.traceExit(Optional.empty());
        }

        var con = dbUtils.getConnection();
        try (var statement = con.prepareStatement("UPDATE orders SET ordered_by=?, delivered=?, ordered_at=?, delivered_at=? WHERE id=?;")) {
            statement.setInt(1, entity.getOrderedBy());
            statement.setBoolean(2, entity.getDelivered());
            statement.setDate(3, entity.getOrderedAt());
            statement.setDate(4, entity.getDeliveredAt());
            statement.setInt(5, entity.getId());

            var result = statement.executeUpdate();
            if (result == 1) {
                _logger.traceExit("Order with ID {} updated.", entity.getId());

                return oldMatch;
            } else {
                _logger.error("No Order with ID: {}.", entity.getId());

                return _logger.traceExit(Optional.empty());
            }
        } catch (SQLException ex) {
            _logger.error(ex);

            return _logger.traceExit(Optional.empty());
        }
    }

    @Override
    public void clear() {
        _logger.traceEntry("Clearing Order repo.");

        var con = dbUtils.getConnection();

        //noinspection SqlWithoutWhere
        try (var statement = con.prepareStatement("DELETE FROM order_details;")) {
            statement.executeUpdate();
        } catch (SQLException ex) {
            _logger.error(ex);
        }

        //noinspection SqlWithoutWhere
        try (var statement = con.prepareStatement("DELETE FROM orders;")) {
            statement.executeUpdate();
        } catch (SQLException ex) {
            _logger.error(ex);
        }

        try (var statement = con.prepareStatement("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='orders' OR NAME='order_details';")) {
            statement.executeUpdate();
        } catch (SQLException ex) {
            _logger.error(ex);
        }

        _logger.traceExit("Order and order_details repo cleared.");
    }

    private Order generateOrderFromResult(ResultSet orders, ResultSet drugs) throws SQLException, IndexOutOfBoundsException {
        _logger.info("Generating Order from result.");

        var resultId = orders.getInt(1);
        var resultOrderedBy = orders.getInt(2);
        var resultDelivered = orders.getBoolean(3);
        var resultOrderedAt = orders.getDate(4);
        var resultDeliveredAt = orders.getDate(5);

        var order = new Order(resultId, resultOrderedBy, resultDelivered, resultOrderedAt, resultDeliveredAt);

        while (drugs.next()) {
            var resultDrugId = drugs.getInt(2);
            var resultQuantity = drugs.getInt(3);

            order.addDrug(resultDrugId, resultQuantity);
        }

        return order;
    }
}
