package com.sarf.task_management_system.domain.exceptions;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(final String message) {
        super(message);
    }
}
