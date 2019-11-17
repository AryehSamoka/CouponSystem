package com.aryeh.CouponSystem.rest;

import com.aryeh.CouponSystem.data.entity.CsErrorResponse;
import com.aryeh.CouponSystem.rest.controller.LoginController;
import com.aryeh.CouponSystem.rest.controller.AdminController;
import com.aryeh.CouponSystem.rest.controller.CompanyController;
import com.aryeh.CouponSystem.rest.controller.CustomerController;
import com.aryeh.CouponSystem.rest.ex.InvalidLoginException;
import com.aryeh.CouponSystem.rest.ex.InvalidRootAdminAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;


@ControllerAdvice(assignableTypes = {AdminController.class, CustomerController.class, CompanyController.class, LoginController.class})
public class CsControllerAdvice {

    @ExceptionHandler(InvalidLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public CsErrorResponse handleUnauthorized(InvalidLoginException ex) {
        return CsErrorResponse.of(HttpStatus.UNAUTHORIZED, "Unauthorized.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public CsErrorResponse handleUserNameExists(DataIntegrityViolationException ex) {
        return CsErrorResponse.of(HttpStatus.CONFLICT, "User name already exists.");
    }


    @ExceptionHandler(InvalidRootAdminAccessException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public CsErrorResponse handleUserNameExists(InvalidRootAdminAccessException ex) {
        return CsErrorResponse.of(HttpStatus.UNAUTHORIZED, "You aren't authorized to change root administrator.");
    }
}
