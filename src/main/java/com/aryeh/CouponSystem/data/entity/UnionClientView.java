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
@Subselect(value = "SELECT cl1.id as my_view_id, cl1.email, cl1.password from client cl1 inner join company co on cl1.id=co.id union " +
        "SELECT cl2.id as my_view_id , cl2.email, cl2.password from client cl2 inner join customer cu on cl2.id=cu.id"
)
@org.hibernate.annotations.Synchronize({"Client", "Company"})
@Table(name = "union_client_view")
public class UnionClientView implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "my_view_id")
    private Long myViewId;
    @NotNull
    @Column(name = "email")
    private String email;
    @NotNull
    @Column(name = "password")
    private String password;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
