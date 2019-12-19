package com.aryeh.CouponSystem.data.entity;

import com.aryeh.CouponSystem.Service.CompanyServiceImpl;
import com.aryeh.CouponSystem.rest.ClientSession;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.springframework.context.ApplicationContext;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Entity
@Table(name = "company")
public class Company extends Client {
    public static final long NO_ID = -1;

    private String name;
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private Set<Coupon> coupons;

    public Company() {
        coupons = new HashSet<>();
    }

    public Company(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public static Company empty() {
        Company company = new Company();
        company.setId(NO_ID);
        return company;
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Coupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(Set<Coupon> jsonCoupons) {
        coupons = jsonCoupons;

        for (Coupon coupon : coupons) {
            coupon.setCompany(this);
        }
    }

    @Override
    public void setClientSession(ApplicationContext context, ClientSession clientSession) {
        CompanyServiceImpl service = context.getBean(CompanyServiceImpl.class);
        service.setClientId(id);
        clientSession.setService(service);
        clientSession.setClientType(ClientType.COMPANY);
    }
}
