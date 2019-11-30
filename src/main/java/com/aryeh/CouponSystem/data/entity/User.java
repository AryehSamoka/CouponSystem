package com.aryeh.CouponSystem.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.MetaValue;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class User {
    public static final long NO_ID = -1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true, length = 32, nullable = false)
    private String email;
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Any(metaColumn = @Column(name="role"))
    @AnyMetaDef(idType = "long", metaType = "int",
    metaValues = {@MetaValue(value = "1", targetEntity = Company.class),
                  @MetaValue(value = "2", targetEntity = Customer.class),
                  @MetaValue(value = "-1", targetEntity = Admin.class)})
//    @OneToOne
    @JoinColumn(name = "client_id")
    @JsonIgnore
    private Client client;

    public User() {
    }

    public User(Client client) {
        this.client = client;
        email = client.getEmail();
        password = client.returnPassword();

    }

    public static User empty() {
        User user = new User();
        user.setId(NO_ID);
        return user;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void update(Client client) {
        setEmail(client.getEmail());
        setPassword(client.returnPassword());
    }
}
