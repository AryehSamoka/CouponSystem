package com.aryeh.CouponSystem.rest.ex;

public class invalidEmailException extends RuntimeException {
    public invalidEmailException(String msg) {
        super(msg);
    }
}
