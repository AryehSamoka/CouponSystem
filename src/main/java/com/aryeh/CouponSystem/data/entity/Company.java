package com.aryeh.CouponSystem.data.entity;

import com.aryeh.CouponSystem.Service.CompanyServiceImpl;
import com.aryeh.CouponSystem.rest.ClientSession;
import com.fasterxml.jackson.annotation.*;
import org.springframework.context.ApplicationContext;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Entity
@Table(name = "company")
public class Company extends Client {
    public static final long NO_ID = -1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @Column(unique = true, length = 32, nullable = false)
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Coupon> coupons;

    public Company() {
        coupons = new ArrayList<>();
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

    public List<Coupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<Coupon> jsonCoupons) {
        coupons = jsonCoupons;

        for (Coupon coupon : coupons) {
            coupon.setCompany(this);
        }
    }

    @Override
    public void setClientSession(ApplicationContext context, ClientSession clientSession) {
        CompanyServiceImpl service = context.getBean(CompanyServiceImpl.class);
        service.setCompanyId(id);
        clientSession.setService(service);
    }
}
