package com.simpleclouddisk.exception.service;

import com.simpleclouddisk.exception.ServiceException;

public class PasswordException extends ServiceException {
    public PasswordException(String message) {
        super(message);
    }
}
