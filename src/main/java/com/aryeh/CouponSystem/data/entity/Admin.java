package com.aryeh.CouponSystem.data.entity;

import com.aryeh.CouponSystem.Service.AdminServiceImpl;
import com.aryeh.CouponSystem.rest.ClientSession;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.context.ApplicationContext;

import javax.persistence.*;

import static com.aryeh.CouponSystem.data.entity.ClientType.ADMIN;

@Entity
@Table(name = "admin")
public class Admin extends Client{
    public static final long NO_ID = -1;

    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public Admin() {
    }

    public Admin(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static Admin empty() {
        Admin admin = new Admin();
        admin.setId(NO_ID);
        return admin;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonGetter("password")
    public String getPassword() {
        return password;
    }

    public String returnPassword(){
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void setClientSession(ApplicationContext context, ClientSession clientSession) {
        AdminServiceImpl service = context.getBean(AdminServiceImpl.class);
        service.setClientId(id);
        clientSession.setService(service);
        clientSession.setClientType(ClientType.ADMIN);
    }
}
