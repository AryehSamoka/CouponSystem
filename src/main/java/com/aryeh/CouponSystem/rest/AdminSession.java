package com.aryeh.CouponSystem.rest;

import com.aryeh.CouponSystem.Service.AdminServiceImpl;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AdminSession extends AbsSession {
    public AdminServiceImpl service;
    private long lastAccessedMillis;

    public AdminServiceImpl getService() {
        return service;
    }

    public void setService(AdminServiceImpl service) {
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
