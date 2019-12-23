package com.aryeh.CouponSystem.Service;

import com.aryeh.CouponSystem.data.entity.Company;
import com.aryeh.CouponSystem.data.entity.Coupon;

import java.time.LocalDate;
import java.util.List;

public interface CompanyService {
    Company findById();

    void deleteById();

    Company update(Company company);

    Coupon addCoupon(Coupon coupon);

    Company addCoupons(List<Coupon> coupons);

    Coupon updateCoupon(Coupon coupon);

    void deleteCoupon(long couponId);

    List<Coupon> findCompanyCoupons();

    List<Coupon> findCompanyCouponsByCategory(int category);

    List<Coupon> findCompanyCouponsLessThan(double price);

    List<Coupon> findCompanyCouponsBeforeDate(LocalDate date);

    List<String> findEmailsMyCustomers();
}
