package com.aryeh.CouponSystem.rest;

import com.aryeh.CouponSystem.Service.AbsService;
import com.aryeh.CouponSystem.data.entity.Client;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ClientSession {
    private Client client;
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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
