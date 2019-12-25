package com.aryeh.CouponSystem.rest.ex;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String msg) {
        super((msg));
    }
}
