package com.aryeh.CouponSystem.rest;

import com.aryeh.CouponSystem.data.entity.CsErrorResponse;
import com.aryeh.CouponSystem.rest.controller.LoginController;
import com.aryeh.CouponSystem.rest.controller.AdminController;
import com.aryeh.CouponSystem.rest.controller.CompanyController;
import com.aryeh.CouponSystem.rest.controller.CustomerController;
import com.aryeh.CouponSystem.rest.ex.*;
import org.springframework.dao.DataIntegrityViolationException;
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
        return CsErrorResponse.of(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(InvalidAccessException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public CsErrorResponse handleInvalidAccess(InvalidAccessException ex) {
        return CsErrorResponse.of(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CsErrorResponse handleUserNameExists(DataIntegrityViolationException ex) {
        return CsErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getRootCause().getMessage());
    }


    @ExceptionHandler(InvalidRootAdminAccessException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public CsErrorResponse handleRootAdminAccess(InvalidRootAdminAccessException ex) {
        return CsErrorResponse.of(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(NoSuchCompanyException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public CsErrorResponse handleCompanyNonExists(NoSuchCompanyException ex) {
        return CsErrorResponse.of(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(NoSuchCustomerException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public CsErrorResponse handleCustomerNonExists(NoSuchCustomerException ex) {
        return CsErrorResponse.of(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(NoSuchCouponException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public CsErrorResponse handleCouponNonExists(NoSuchCouponException ex) {
        return CsErrorResponse.of(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ZeroCouponAmountException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ResponseBody
    public CsErrorResponse handleCouponNonExists(ZeroCouponAmountException ex) {
        return CsErrorResponse.of(HttpStatus.NOT_ACCEPTABLE, ex.getMessage());
    }

    @ExceptionHandler(InvalidCouponAccessException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public CsErrorResponse handleRootAdminAccess(InvalidCouponAccessException ex) {
        return CsErrorResponse.of(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(invalidIdException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public CsErrorResponse handleInvalidUpdate(invalidIdException ex) {
        return CsErrorResponse.of(HttpStatus.NOT_FOUND, ex.getMessage());
    }
}
