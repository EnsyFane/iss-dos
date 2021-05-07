package domain.validation;

import domain.models.Drug;

public class DrugValidator extends AbstractValidator<Integer, Drug> {
    @Override
    protected String validateEntity(Drug entity) {
        var message = "";

        if (entity.getId() < 0) {
            message += "Drug id can not be negative.\n";
        }
        if (entity.getName().length() == 0) {
            message += "Drug name property can not be empty.\n";
        }
        if (entity.getInStock() < 0) {
            message += "Drug in stock property can not be negative.\n";
        }

        return message;
    }
}
