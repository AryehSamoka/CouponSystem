package com.aryeh.CouponSystem.data.repository;

import com.aryeh.CouponSystem.data.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("select cu from Customer cu left join cu.coupons co where co is null ")
    List<Customer> findAllCustomersWithoutCoupons();

    @Query("select distinct t1.email from Company as t1 join t1.coupons as t2 join t2.customers as t3 where t3.id=:customerId")
    List<String> findEmailsMyCompanies(long customerId);
}
