package repository;

import domain.models.Order;

public interface IOrderRepository extends IRepository<Integer, Order> {
    void placeOrder(Order order);
}
