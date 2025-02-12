package com.sarf.task_management_system.domain.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(final String message) {
        super(message);
    }

}
