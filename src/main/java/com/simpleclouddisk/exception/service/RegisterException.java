package com.simpleclouddisk.exception.service;

import com.simpleclouddisk.exception.ServiceException;

public class RegisterException extends ServiceException {
    public RegisterException(String message) {
        super(message);
    }
}
