package com.kuro.expensetracker.exceptions;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);

    }

    public EntityNotFoundException(Class<?> entity, Long id) {
        super(entity.getSimpleName() + " with id #" + id + " not found!");
    }

    public EntityNotFoundException(Class<?> entity, String name) {
        super(entity.getSimpleName() + " with name [" + name + "] not found!");
    }
}
