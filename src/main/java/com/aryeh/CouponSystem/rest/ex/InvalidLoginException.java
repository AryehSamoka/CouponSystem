package com.aryeh.CouponSystem.rest.ex;

public class InvalidLoginException extends RuntimeException {
    public InvalidLoginException(String msg) {
        super(msg);
    }
}
