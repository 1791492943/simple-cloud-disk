package com.simpleclouddisk.exception.service;

import com.simpleclouddisk.exception.ServiceException;

public class LoginException extends ServiceException {
    public LoginException(String message) {
        super(message);
    }
}
