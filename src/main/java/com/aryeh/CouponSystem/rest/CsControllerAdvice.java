package com.aryeh.CouponSystem.rest;

import com.aryeh.CouponSystem.data.entity.CsErrorResponse;
import com.aryeh.CouponSystem.rest.controller.LoginController;
import com.aryeh.CouponSystem.rest.controller.AdminController;
import com.aryeh.CouponSystem.rest.controller.CompanyController;
import com.aryeh.CouponSystem.rest.controller.CustomerController;
import com.aryeh.CouponSystem.rest.ex.InvalidLoginException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice(assignableTypes = {AdminController.class, CustomerController.class, CompanyController.class, LoginController.class})
public class CsControllerAdvice {

    @ExceptionHandler(InvalidLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public CsErrorResponse handleUnauthorized(InvalidLoginException ex) {
        return CsErrorResponse.of(HttpStatus.UNAUTHORIZED, "Unauthorized.");
    }
}
