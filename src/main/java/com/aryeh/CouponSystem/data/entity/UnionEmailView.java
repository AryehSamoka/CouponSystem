package com.aryeh.CouponSystem.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Immutable
@Subselect(value = "SELECT c.id as my_view_id , c.email from client c inner join company on c.id=company.id union " +
        "SELECT d.id as my_view_id , d.email from client d inner join customer on d.id=customer.id"
)
@org.hibernate.annotations.Synchronize({"Client", "Company"})
@Table(name = "union_email_view")
public class UnionEmailView implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "my_view_id")
    private Long myViewId;
    @NotNull
    @Column(name = "email")
    private String email;

    @JsonIgnore
    public Long getMyViewId() {
        return myViewId;
    }

    public void setMyViewId(Long myViewId) {
        this.myViewId = myViewId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
