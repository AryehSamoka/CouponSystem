package com.aryeh.CouponSystem.rest.ex;

public class NoSuchCustomerException extends RuntimeException {
    public NoSuchCustomerException(String msg) {
        super(msg);
    }
}
