package service;

import domain.dto.UserDTO;
import domain.models.User;

public interface IDOSService {
    User loginUser(UserDTO loginDetails, IClientObserver client) throws ServerException;

    void logoutUser(String username);

    User addUser(User toAdd);

    boolean changePassword(Integer userId, String oldPassword, String newPassword);
}
