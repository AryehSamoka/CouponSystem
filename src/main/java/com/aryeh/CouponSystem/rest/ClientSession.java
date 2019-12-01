package com.aryeh.CouponSystem.rest;

import com.aryeh.CouponSystem.Service.AbsService;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ClientSession {
    private AbsService service;
    private long lastAccessedMillis;

    public AbsService getService() {
        return service;
    }

    public void setService(AbsService service) {
        this.service = service;
    }

    public long getLastAccessedMillis() {
        return lastAccessedMillis;
    }

    public void accessed() {
        lastAccessedMillis = System.currentTimeMillis();
    }

    public Long getClientId() {
        return service.getClientId();
    }
}
