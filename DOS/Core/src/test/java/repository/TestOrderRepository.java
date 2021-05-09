package repository;

import domain.models.Drug;
import domain.models.Order;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.TestConstants;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestOrderRepository {
    private static IOrderRepository _orderRepo;

    @BeforeAll
    public static void SetupDB() {
        var props = new Properties();
        try {
            props.load(new FileReader(TestConstants.TEST_JAVA_DIRECTORY + '/' + TestConstants.TEST_CONFIG_FILE));
        } catch (IOException e) {
            System.out.println("Cannot find " + TestConstants.TEST_CONFIG_FILE + ".\n");
            e.printStackTrace();
            return;
        }

        _orderRepo = new OrderRepository(props.getProperty("jdbc.url"));
        _orderRepo.clear();
    }

    @AfterEach
    public void ClearDB() {
        _orderRepo.clear();
    }

    @Test
    public void OrderRepo_GetByIdWithNoStoredId_ReturnsEmpty() {
        var storedInRepo = addOrderToRepo();

        var response = _orderRepo.getById(storedInRepo.getId() + 1);

        assertTrue(response.isEmpty());
    }

    private Order addOrderToRepo() {
        var order = new Order.Builder().build();
        _orderRepo.add(order);
        return order;
    }

    private Order placeOrderToRepo(Map<Integer, Integer> drugs) {
        var order = new Order.Builder().withDrugs(drugs).build();
        _orderRepo.placeOrder(order);
        return order;
    }

    private List<Order> addOrdersToRepo(int amount) {
        var orders = new ArrayList<Order>();
        for (var i = 0; i < amount; i++) {
            var order = new Order.Builder().build();
            _orderRepo.add(order);
            orders.add(order);
        }

        return orders;
    }
}
