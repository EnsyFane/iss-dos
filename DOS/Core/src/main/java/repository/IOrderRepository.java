package repository;

import domain.models.Order;

import java.util.Optional;

public interface IOrderRepository extends IRepository<Integer, Order> {
    Optional<Order> placeOrder(Order order);
}
