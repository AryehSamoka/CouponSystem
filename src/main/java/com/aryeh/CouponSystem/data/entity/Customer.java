package com.aryeh.CouponSystem.data.entity;

import com.aryeh.CouponSystem.Service.CustomerServiceImpl;
import com.aryeh.CouponSystem.rest.ClientSession;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.context.ApplicationContext;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer")
public class Customer extends Client{
    public static final long NO_ID = -1;

    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @ManyToMany(cascade = { CascadeType.DETACH, CascadeType.REFRESH })
    @JoinTable(
            name = "customer_coupon",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "coupon_id"),
            uniqueConstraints = {@UniqueConstraint(columnNames={"customer_id", "coupon_id"})}
            )
    private List<Coupon> coupons;

    public Customer() {
        coupons = new ArrayList<>();
    }

    public Customer(String firstName, String lastName, String email, String password) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public static Customer empty() {
        Customer customer = new Customer();
        customer.setId(NO_ID);
        return customer;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public List<Coupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<Coupon> coupons) {
        this.coupons = coupons;
    }

    public void addCoupon(Coupon coupon) {
        coupon.decrementAmount();
        coupons.add(coupon);
    }

    @Override
    public void setClientSession(ApplicationContext context, ClientSession clientSession) {
        CustomerServiceImpl service = context.getBean(CustomerServiceImpl.class);
        service.setClientId(id);
        clientSession.setService(service);
        clientSession.setClientType(ClientType.CUSTOMER);
        clientSession.accessed();
    }
}
