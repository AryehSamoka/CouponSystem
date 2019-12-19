package com.aryeh.CouponSystem.rest.ex;

public class InvalidCouponAccessException extends RuntimeException {
    public InvalidCouponAccessException(String msg) {
        super(msg);
    }
}
