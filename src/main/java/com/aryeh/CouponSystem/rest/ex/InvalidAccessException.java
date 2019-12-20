package com.aryeh.CouponSystem.rest.ex;

public class InvalidAccessException extends RuntimeException {
    public InvalidAccessException(String msg) {
        super((msg));
    }
}
