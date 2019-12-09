package com.aryeh.CouponSystem.data.repository;

import com.aryeh.CouponSystem.data.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);

    @Query("select t2.category, t1.email, t3.email from Company as t1 join t1.coupons as t2 join t2.customers as t3" +
            " order by t2.category")
    List<String[]> findPairsEmailsOfCompsCustomersOrderedByCategory();

    @Query("select Count(t1.id), t2.category from Company as t1 join t1.coupons as t2 join t2.customers as t3 " +
            "group by t2.category order by Count(t1.id) DESC")
    List<Integer[]> CountPairsByCategory();
}
