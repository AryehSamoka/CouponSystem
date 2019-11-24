package com.aryeh.CouponSystem.data.entity;

import javax.persistence.*;
import java.util.Objects;

@MappedSuperclass
public abstract class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, length = 32)
    protected String email;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return getEmail().equals(client.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail());
    }
}
