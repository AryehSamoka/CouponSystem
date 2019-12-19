package com.aryeh.CouponSystem.rest.ex;

public class NoSuchCompanyException extends RuntimeException {
    public NoSuchCompanyException(String msg) {
        super(msg);
    }
}
