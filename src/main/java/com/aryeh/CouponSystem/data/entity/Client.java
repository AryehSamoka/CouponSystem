package com.aryeh.CouponSystem.data.entity;

import com.aryeh.CouponSystem.rest.ClientSession;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.context.ApplicationContext;

import javax.persistence.*;

@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public abstract class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @Column(unique = true, length = 32)
    protected String email;
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    protected String password;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public abstract String  getPassword();

    public abstract String returnPassword();

    public abstract void setPassword(String password);

    public abstract String getEmail();

    public abstract void setEmail(String email);

    public abstract void setClientSession(ApplicationContext context, ClientSession clientSession);

    /**
     * The password will be checked and if it is null the old password will stay,
     * the reason is that only the password can't be seen by findById because of json ignore.
     * On client to check what will be counted as null.
     * @param oldClient
     */
    public void checkPassword(Client oldClient){
        if(this.getPassword() == null){
            this.setPassword(oldClient.getPassword());
        }
    }
}
