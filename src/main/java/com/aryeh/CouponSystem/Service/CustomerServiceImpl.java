package com.aryeh.CouponSystem.Service;

import com.aryeh.CouponSystem.data.entity.Coupon;
import com.aryeh.CouponSystem.data.entity.Customer;
import com.aryeh.CouponSystem.data.repository.CouponRepository;
import com.aryeh.CouponSystem.data.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CustomerServiceImpl extends AbsService implements CustomerService {
    private long customerId;

    private CouponRepository couponRepository;
    private CustomerRepository customerRepository;

    @Autowired
    public CustomerServiceImpl(CouponRepository couponRepository, CustomerRepository customerRepository) {
        this.couponRepository = couponRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public Customer findById() {
        return customerRepository.findById(customerId)
                .orElse(Customer.empty());
    }

    @Override
    @Transactional
    public void deleteById() {
        customerRepository.deleteById(customerId);
    }


    @Override
    @Transactional
    public Customer update(Customer customer) {
        if (customer.getId() == customerId || customer.getId() == 0) {
            customer.setId(customerId);
            customer.setCoupons(Collections.emptyList());
            return customerRepository.save(customer);
        }
        return Customer.empty();
    }

    @Override
    @Transactional
    public List<Coupon> findCustomerCoupons() {
        return couponRepository.findCustomerCoupons(customerId);
    }

    @Override
    @Transactional
    public List<Coupon> findCustomerCouponsByCategory(int category) {
        return couponRepository.findCustomerCouponsByCategory(customerId, category);
    }

    @Override
    @Transactional
    public List<Coupon> findCustomerCouponsLessThan(double price) {
        return couponRepository.findCustomerCouponsLessThan(customerId, price);
    }

    @Override
    @Transactional
    public List<Coupon> findExpiredCoupons() {
        LocalDate date = LocalDate.now();
        return couponRepository.findByEndDateBefore(date);
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }
}
