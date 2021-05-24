package service;

import domain.dto.DrugDTO;
import domain.dto.OrderDTO;
import domain.dto.UserDTO;
import domain.models.Order;
import domain.models.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.IDrugRepository;
import repository.IOrderRepository;
import repository.IUserRepository;
import utils.Constants;
import utils.PasswordUtils;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DOSService implements IDOSService {
    private final IUserRepository userRepo;
    private final IDrugRepository drugRepo;
    private final IOrderRepository orderRepo;

    private final Map<String, IClientObserver> loggedClients;

    private static final Logger _logger = LogManager.getLogger();

    public DOSService(IUserRepository userRepo, IDrugRepository drugRepo, IOrderRepository orderRepo) {
        _logger.info("Initializing DOS Service.");

        this.userRepo = userRepo;
        this.drugRepo = drugRepo;
        this.orderRepo = orderRepo;

        loggedClients = new ConcurrentHashMap<>();
    }

    @Override
    public User loginUser(UserDTO loginDetails, IClientObserver client) {
        _logger.traceEntry("Trying to log in user: {}.", loginDetails.getUserName());

        var userDetails = userRepo.getByUsername(loginDetails.getUserName());
        if (userDetails.isEmpty()) {
            _logger.traceExit("No user that matches credentials found.");

            return null;
        }

        var encryptedPassword = PasswordUtils.encryptPassword(loginDetails.getPassword(), userDetails.get().getSalt());
        if (encryptedPassword.equals(userDetails.get().getEncryptedPassword())) {

            if (loggedClients.get(loginDetails.getUserName()) != null) {
                _logger.warn("User already logged in.");

                throw _logger.traceExit(new ServerException("User already logged in."));
            }

            if (client == null) {
                _logger.error("Client is null.");
            } else {
                loggedClients.put(loginDetails.getUserName(), client);
            }

            _logger.traceExit("Logged in user {}.", userDetails.get());
            return userDetails.get();
        }

        _logger.traceExit("No user that matches credentials found.");

        return null;
    }

    @Override
    public void logoutUser(String username) {
        _logger.info("Logging user: {} out.", username);

        var localClient = loggedClients.remove(username);
        if (localClient == null) {
            _logger.warn("No user logged in with username {}.", username);
        }
    }

    @Override
    public User addUser(User toAdd) {
        _logger.traceEntry("Adding user.");

        if (toAdd.getSalt().isEmpty()) {
            toAdd.setSalt(PasswordUtils.generateSalt(Constants.SALT_LENGTH));
            toAdd.setEncryptedPassword(PasswordUtils.encryptPassword(toAdd.getEncryptedPassword(), toAdd.getSalt()));
        }

        var response = userRepo.add(toAdd);

        if (response.isEmpty()) {
            _logger.traceExit("User added.");
            return null;
        }

        _logger.traceExit("User not added.");

        return response.get();
    }

    @Override
    public boolean changePassword(Integer userId, String oldPassword, String newPassword) {
        _logger.traceEntry("Trying to change the password for {}.", userId);

        var user = userRepo.getById(userId);

        if (user.isEmpty()) {
            _logger.traceExit("No user with given id.");

            return false;
        }

        if (!user.get().getEncryptedPassword().equals(PasswordUtils.encryptPassword(oldPassword, user.get().getSalt()))) {
            _logger.traceExit("Old password isn't correct.");

            return false;
        }

        user.get().setEncryptedPassword(PasswordUtils.encryptPassword(newPassword, user.get().getSalt()));
        user.get().setNextPasswordChange(new Date(System.currentTimeMillis() + Constants.DISTANCE_BETWEEN_PASSWORD_CHANGES));

        var result = userRepo.update(user.get());
        if (result.isEmpty()) {
            _logger.traceExit("User could not be updated.");

            return false;
        }

        _logger.traceExit("Password changed.");

        return true;
    }

    @Override
    public List<DrugDTO> getAvailableDrugs() {
        _logger.traceEntry("Getting all available drugs.");

        var drugs = drugRepo.getAvailableDrugs();
        var converted = drugs
                .stream()
                .map(d -> new DrugDTO(d.getId(),false, d.getName(), d.getDescription(), d.getInStock(), 0))
                .collect(Collectors.toList());

        _logger.traceExit("Got {} drugs.", drugs.size());

        return converted;
    }

    @Override
    public boolean placeOrder(Order order) {
        _logger.traceEntry("Placing order. {}", order);

        if (order.getDeliveredAt() == null) {
            order.setDeliveredAt(new Date(System.currentTimeMillis() + Constants.DEFAULT_DRUG_DELIVERY_ETA));
        }

        var result = orderRepo.placeOrder(order);

        if (result.isEmpty()) {
            _logger.traceExit("Order placed.");
        } else {
            _logger.traceExit("Order not placed.");
        }

        return result.isEmpty();
    }

    @Override
    public List<OrderDTO> getOrders() {
        _logger.traceEntry("Getting all orders.");

        var orders = orderRepo.getAll();
        var converted = orders
                .stream()
                .map(d -> {
                    var user = userRepo.getById(d.getOrderedBy());
                    var name = user.map(value -> value.getFirstName() + " " + value.getLastName()).orElseGet(() -> d.getOrderedBy().toString());
                    return new OrderDTO(d.getId(), name, d.getDelivered(), d.getOrderedAt(), d.getDeliveredAt());
                })
                .collect(Collectors.toList());

        _logger.traceExit("Got {} orders.", orders.size());

        return converted;
    }

    @Override
    public boolean updateUser(User updatedUser) {
        _logger.traceEntry("Adding user.");

        var response = userRepo.update(updatedUser);

        if (response.isEmpty()) {
            _logger.traceExit("User could not be updated.");
            return false;
        }

        return true;
    }

    @Override
    public void completeOrder(Integer orderId) {
        var order = orderRepo.getById(orderId);

        if (order.isPresent()) {
            order.get().setDelivered(true);
            orderRepo.update(order.get());
        }
    }

    @Override
    public void cancelOrder(Integer orderId) {
        orderRepo.remove(orderId);
    }
}
