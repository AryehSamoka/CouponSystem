package com.aryeh.CouponSystem.threads;

import com.aryeh.CouponSystem.Service.CustomerService;
import com.aryeh.CouponSystem.data.entity.Coupon;
import com.aryeh.CouponSystem.data.repository.CouponRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CouponCleanerTask implements Runnable {

    private boolean run = true;
    private static final long DAY_IN_MILLIS = 60 * 60 * 24 * 1000;
    private CustomerService customerService;
    private CouponRepository couponRepository;

    public CouponCleanerTask(CustomerService customerService, CouponRepository couponRepository) {
        this.customerService = customerService;
        this.couponRepository = couponRepository;
    }

    @Override
    public void run() {

        while (run) {
            List<Coupon> expiredCoupons = customerService.findExpiredCoupons();
            Iterator<Coupon> it = expiredCoupons.iterator();

            while (it.hasNext()) {
                couponRepository.delete(it.next());
            }

            try {
                Thread.sleep(DAY_IN_MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

