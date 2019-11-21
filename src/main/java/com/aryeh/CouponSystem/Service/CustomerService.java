package com.aryeh.CouponSystem.Service;

import com.aryeh.CouponSystem.data.entity.Coupon;
import com.aryeh.CouponSystem.data.entity.Customer;

import java.util.List;

public interface CustomerService {
    Customer findById();

    void deleteById();

    Customer update(Customer customer);

    Customer addCoupon(long couponId);

    List<Coupon> findCustomerCoupons();

    List<Coupon> findCustomerCouponsByCategory(int category);

    List<Coupon> findCustomerCouponsLessThan(double price);

    List<Coupon> findExpiredCoupons();
}
