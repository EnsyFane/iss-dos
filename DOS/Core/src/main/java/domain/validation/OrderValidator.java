package domain.validation;

import domain.models.Order;

import java.util.Date;

public class OrderValidator extends AbstractValidator<Integer, Order> {
    @Override
    protected String validateEntity(Order entity) {
        var message = "";

        if (entity.getId() < 0) {
            message += "Order id can not be negative.\n";
        }
        if (entity.getOrderedBy() < 0) {
            message += "Order ordered by property can not be negative.\n";
        }
        if (entity.getOrderedAt().after(new Date(System.currentTimeMillis() + 10))) {
            message += "Order can not pe placed after the current time.\n";
        }

        return message;
    }
}
