package com.kuro.expensetracker.exceptions;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);

    }

    public EntityNotFoundException(Object entity, Long id) {
        super(entity.getClass().getName() + " with id #" + id + " not found!");
    }

    public EntityNotFoundException(Object entity, String name) {
        super(entity.getClass().getName() + " with name \"" + name + "\" not found!");
    }
}
