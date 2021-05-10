package repository;

import domain.models.Drug;
import domain.models.Order;
import domain.validation.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.TestConstants;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void OrderRepo_GetById_ReturnsCorrectOrder() {
        var storedInRepo = addOrderToRepo();

        var response = _orderRepo.getById(storedInRepo.getId());

        assertTrue(response.isPresent());
        assertEquals(storedInRepo, response.get());
    }

    @Test
    public void OrderRepoWithMultipleDrugs_GetById_ReturnsCorrectOrder() {
        var orders = addOrdersToRepo(4);

        var actualOrder0 = _orderRepo.getById(orders.get(0).getId());
        var actualOrder1 = _orderRepo.getById(orders.get(1).getId());

        assertTrue(actualOrder0.isPresent());
        assertTrue(actualOrder1.isPresent());
        assertEquals(orders.get(0), actualOrder0.get());
        assertEquals(orders.get(1), actualOrder1.get());
    }

    @Test
    public void EmptyDrugRepo_AddDrug_DrugIsAdded() {
        var order = new Order.Builder().build();

        var response = _orderRepo.add(order);
        var allOrders = _orderRepo.getAll();

        assertTrue(response.isEmpty());
        assertEquals(1, allOrders.size());
        assertEquals(order, allOrders.get(0));
    }

    @Test
    public void OrderRepo_AddInvalidOrder_ValidationExceptionIsThrown() {
        var order = new Order.Builder().withId(-1).build();

        var exception = assertThrows(ValidationException.class, () -> _orderRepo.add(order));
        assertEquals("Order id can not be negative.", exception.getMessage());
    }

    @Test
    public void OrderRepo_AddNullOrder_IllegalArgumentExceptionThrown() {
        var exception = assertThrows(IllegalArgumentException.class, () -> _orderRepo.add(null));
        assertEquals("Null Order received.", exception.getMessage());
    }

    @Test
    public void OrderRepoWithMultipleOrders_GetAll_ReturnsAllStoredOrders() {
        var drugs = addOrdersToRepo(50);

        var actualOrders = _orderRepo.getAll();

        assertEquals(50, actualOrders.size());
        assertEquals(drugs, actualOrders);
    }

    @Test
    public void OrderRepoWithOrder_Remove_OrderIsDeleted() {
        var order = addOrderToRepo();

        var deleted = _orderRepo.remove(order.getId());
        var inDatabase = _orderRepo.getById(order.getId());

        assertTrue(inDatabase.isEmpty());
        assertTrue(deleted.isPresent());
        assertEquals(order, deleted.get());
    }

    @Test
    public void OrderRepo_UpdateOrder_OrderIsUpdated() {
        var order = addOrderToRepo();
        var updatedOrder = new Order.Builder().from(order).withOrderedBy(order.getOrderedBy() + 1).build();

        var oldOrder = _orderRepo.update(updatedOrder);
        var inDatabase = _orderRepo.getById(updatedOrder.getId());

        assertTrue(oldOrder.isPresent());
        assertTrue(inDatabase.isPresent());
        assertEquals(order, oldOrder.get());
        assertEquals(updatedOrder, inDatabase.get());
    }

    @Test
    public void OrderRepo_UpdateWithInvalidOrder_ValidationExceptionIsThrown() {
        addOrdersToRepo(2);
        var order = new Order.Builder().withId(-1).build();

        var exception = assertThrows(ValidationException.class, () -> _orderRepo.update(order));
        assertEquals("Order id can not be negative.", exception.getMessage());
    }

    @Test
    public void OrderRepo_UpdateWithNullOrder_IllegalArgumentExceptionThrown() {
        addOrdersToRepo(2);

        var exception = assertThrows(IllegalArgumentException.class, () -> _orderRepo.update(null));
        assertEquals("Null Order received.", exception.getMessage());
    }

    @Test
    public void OrderRepo_PlaceOrder_OrderIsCorrectlyPlaced() {
        var drugs = new HashMap<Integer, Integer>();
        drugs.put(1, 1);
        drugs.put(2, 3);
        var order = new Order.Builder().withDrugs(drugs).build();

        var result = _orderRepo.placeOrder(order);
        var actualOrder = _orderRepo.getById(order.getId());

        assertTrue(result.isEmpty());
        assertTrue(actualOrder.isPresent());
        assertEquals(order, actualOrder.get());
    }

    private Order addOrderToRepo() {
        var order = new Order.Builder().build();
        _orderRepo.add(order);
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
