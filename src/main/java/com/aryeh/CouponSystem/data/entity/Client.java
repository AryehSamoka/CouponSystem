package com.aryeh.CouponSystem.data.entity;

import com.aryeh.CouponSystem.rest.ClientSession;
import org.springframework.context.ApplicationContext;

import javax.persistence.*;
import java.util.Objects;

//@Inheritance(strategy = InheritanceType.JOINED)
//@Entity
@MappedSuperclass
public abstract class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, length = 32)
    protected String email;
//    @OneToOne(mappedBy = "client" , cascade = CascadeType.ALL)
//    protected User user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public abstract String returnPassword();

    public abstract void setPassword(String password);

    public abstract String getEmail();

    public abstract void setEmail(String email);

    public abstract void setClientSession(ApplicationContext context, ClientSession clientSession);
}
