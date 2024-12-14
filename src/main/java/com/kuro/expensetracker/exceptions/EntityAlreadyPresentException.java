package com.kuro.expensetracker.exceptions;

public class EntityAlreadyPresentException extends RuntimeException {
    public EntityAlreadyPresentException(String message) {
        super(message);

    }

    public EntityAlreadyPresentException(Class entity, String  name) {
        super(entity.getSimpleName() + " with name \"" + name + "\" not found!");
    }
}
