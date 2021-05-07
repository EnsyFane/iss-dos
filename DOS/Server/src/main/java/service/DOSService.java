package service;

import domain.dto.UserDTO;
import domain.models.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.IDrugRepository;
import repository.IUserRepository;
import utils.Constants;
import utils.PasswordUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DOSService implements IDOSService {
    private final IUserRepository userRepo;
    private final IDrugRepository drugRepo;

    private final Map<String, IClientObserver> loggedClients;

    private static final Logger _logger = LogManager.getLogger();

    public DOSService(IUserRepository userRepo, IDrugRepository drugRepo) {
        _logger.info("Initializing DOS Service.");

        this.userRepo = userRepo;
        this.drugRepo = drugRepo;

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

        toAdd.setSalt(PasswordUtils.generateSalt(Constants.SALT_LENGTH));
        toAdd.setEncryptedPassword(PasswordUtils.encryptPassword(toAdd.getEncryptedPassword(), toAdd.getSalt()));

        var response = userRepo.add(toAdd);

        if (response.isEmpty()) {
            _logger.traceExit("User added.");
            return null;
        }

        _logger.traceExit("User not added.");

        return response.get();
    }
}
