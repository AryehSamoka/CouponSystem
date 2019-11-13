package com.aryeh.CouponSystem.rest;

import com.aryeh.CouponSystem.Service.CompanyService;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CompanySession extends AbsSession {
    private CompanyService service;
    private long lastAccessedMillis;

    public CompanyService getService() {
        return service;
    }

    public void setService(CompanyService service) {
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
