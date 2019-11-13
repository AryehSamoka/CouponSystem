package com.aryeh.CouponSystem.rest;

import com.aryeh.CouponSystem.Service.CustomerService;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CustomerSession extends AbsSession{
    private CustomerService service;
    private long lastAccessedMillis;

    public CustomerService getService() {
        return service;
    }

    public void setService(CustomerService service) {
        this.service = service;
    }

    @Override
    public long getLastAccessedMillis() {
        return lastAccessedMillis;
    }

    public void accessed() {
        lastAccessedMillis = System.currentTimeMillis();
    }
}
