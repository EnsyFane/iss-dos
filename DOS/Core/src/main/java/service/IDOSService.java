package service;

import domain.dto.DrugDTO;
import domain.dto.OrderDTO;
import domain.dto.UserDTO;
import domain.models.Drug;
import domain.models.Order;
import domain.models.User;

import java.util.List;

public interface IDOSService {
    User loginUser(UserDTO loginDetails, IClientObserver client) throws ServerException;

    void logoutUser(String username);

    User addUser(User toAdd);

    boolean changePassword(Integer userId, String oldPassword, String newPassword);

    List<DrugDTO> getAvailableDrugs();

    boolean placeOrder(Order order);

    List<OrderDTO> getOrders();

    boolean updateUser(User updatedUser);

    void completeOrder(Integer orderId);

    void cancelOrder(Integer orderId);
}
