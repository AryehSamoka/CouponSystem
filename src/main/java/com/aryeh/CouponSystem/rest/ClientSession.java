package com.aryeh.CouponSystem.rest;

import com.aryeh.CouponSystem.Service.AbsService;
import com.aryeh.CouponSystem.data.entity.ClientType;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ClientSession {
    private ClientType clientType;
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

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }
}
