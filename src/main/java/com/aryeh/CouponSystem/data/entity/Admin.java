package com.aryeh.CouponSystem.data.entity;

import com.aryeh.CouponSystem.Service.AdminServiceImpl;
import com.aryeh.CouponSystem.rest.ClientSession;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.context.ApplicationContext;

import javax.persistence.*;
import java.lang.annotation.Inherited;

@Entity
@Table(name = "admin")
public class Admin extends Client{
    public static final long NO_ID = -1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true, length = 32, nullable = false)
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;
//    @OneToOne(mappedBy = "client" ,cascade = CascadeType.ALL)
    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "user_id")
    private User user;

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
        setUser();
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

    public User getUser() {
        return user;
    }

    public void setUser() {
        System.out.println("set user");
        if(user == null){
            user = User.empty();
        }
        user.setEmail(email);
        user.setPassword(password);
        user.setClient(this);
    }

    @Override
    public void setClientSession(ApplicationContext context, ClientSession clientSession) {
        AdminServiceImpl service = context.getBean(AdminServiceImpl.class);
        service.setAdminId(id);
        clientSession.setService(service);
    }
}
