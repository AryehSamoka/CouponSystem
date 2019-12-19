package com.aryeh.CouponSystem.data.entity;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Entity
@Table(name = "coupon")
public class Coupon {
    public static final long NO_ID = -1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @JsonIgnore
    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "company_id")
    private Company company;
    private String title;
    @JsonProperty("start_date")
    private LocalDate startDate;
    @Column(nullable = false)
    @JsonProperty("end_date")
    private LocalDate endDate;
    private int category;
    private int amount;
    private String description;
    private double price;
    @Column(name = "image_URL")
    @JsonProperty("image_url")
    private String imageURL;
    @JsonIgnore
    @ManyToMany(cascade = { CascadeType.DETACH, CascadeType.REFRESH })
    @JoinTable(
            name = "customer_coupon",
            joinColumns = @JoinColumn(name = "coupon_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id"),
            uniqueConstraints = {@UniqueConstraint(columnNames={"customer_id", "coupon_id"})}
            )
    private Set<Customer> customers;

    public Coupon() {
        customers = new HashSet<>();
    }

    public Coupon(Company company, String title, LocalDate startDate, LocalDate endDate, int category, int amount,
                  String description, double price, String imageURL) {
        this();
        this.company = company;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.price = price;
        this.imageURL = imageURL;
    }

    public static Coupon empty() {
        Coupon coupon = new Coupon();
        coupon.setId(NO_ID);
        return coupon;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("company_id")
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Set<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(Set<Customer> customers) {
        this.customers = customers;
    }

    public void decrementAmount() {
        --amount;
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "id=" + id +
                ", company=" + company +
                ", title='" + title + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", category=" + category +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", imageURL='" + imageURL + '\'' +
                '}';
    }
}
