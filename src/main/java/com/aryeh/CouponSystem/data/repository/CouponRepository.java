package com.aryeh.CouponSystem.data.repository;

import com.aryeh.CouponSystem.data.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon,Long> {
    List<Coupon> findByCompanyId(long companyId);

    List<Coupon> findByCompanyIdAndCategory(long companyId, int category);

    List<Coupon> findByCompanyIdAndPriceLessThan(long companyId, double price);

    List<Coupon> findByCompanyIdAndEndDateBefore(long companyId, LocalDate date);

    List<Coupon> findByEndDateBefore(LocalDate date);

    @Query("select c from Customer as cust join cust.coupons as c where cust.id=:customerId")
    List<Coupon> findCustomerCoupons(long customerId);

    @Query("select c from Customer as cust join cust.coupons as c where cust.id=:customerId and c.category=:category")
    List<Coupon> findCustomerCouponsByCategory(long customerId, int category);

    @Query("select c from Customer as cust join cust.coupons as c where cust.id=:customerId and c.price<:price")
    List<Coupon> findCustomerCouponsLessThan(long customerId, double price);
}
