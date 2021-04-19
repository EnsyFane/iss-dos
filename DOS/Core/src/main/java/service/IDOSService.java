package service;

import domain.dto.UserDTO;
import domain.models.User;

public interface IDOSService {
    User loginUser(UserDTO loginDetails, IClientObserver client) throws ServerException;
}
