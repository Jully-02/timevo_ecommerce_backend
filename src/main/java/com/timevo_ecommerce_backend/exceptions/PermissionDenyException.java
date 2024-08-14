package com.timevo_ecommerce_backend.exceptions;

public class PermissionDenyException extends Exception{
    public  PermissionDenyException (String message) {
        super(message);
    }
}
