package com.aryeh.CouponSystem.Service;

import com.aryeh.CouponSystem.data.entity.Coupon;
import com.aryeh.CouponSystem.data.entity.Customer;
import com.aryeh.CouponSystem.data.entity.User;
import com.aryeh.CouponSystem.data.repository.CouponRepository;
import com.aryeh.CouponSystem.data.repository.CustomerRepository;
import com.aryeh.CouponSystem.data.repository.UserRepository;
import com.aryeh.CouponSystem.rest.ex.NoSuchCouponException;
import com.aryeh.CouponSystem.rest.ex.ZeroCouponAmountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CustomerServiceImpl extends AbsService implements CustomerService {
    private long customerId;

    private CouponRepository couponRepository;
    private CustomerRepository customerRepository;
    private UserRepository userRepository;

    @Autowired
    public CustomerServiceImpl(CouponRepository couponRepository, CustomerRepository customerRepository,
                               UserRepository userRepository) {
        this.couponRepository = couponRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
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
        deleteCustomerUser();
        customerRepository.deleteById(customerId);
    }

    @Override
    @Transactional
    public Customer update(Customer customer) {
        if (customer.getId() == customerId || customer.getId() == 0) {

            customer.setId(customerId);
            customer.setCoupons(Collections.emptyList());
            Customer oldCustomer = findById();

            checkPassword(customer, oldCustomer);
            updateCustomerUser(customer, oldCustomer);
            return customerRepository.save(customer);
        }
        return Customer.empty();
    }

    @Override
    @Transactional
    public Customer addCoupon(long couponId) {
        Optional<Coupon> optionalCoupon = couponRepository.findById(couponId);
        if (!optionalCoupon.isPresent()) {
            throw new NoSuchCouponException("");
        }

        Coupon coupon = optionalCoupon.get();
        if (coupon.getAmount() <= 0) {
            throw new ZeroCouponAmountException("");
        }
        Customer customer = findById();

        customer.addCoupon(coupon);
        return customerRepository.save(customer);
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
        return couponRepository.findByEndDateBefore(LocalDate.now());
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    private void deleteCustomerUser() {
        Customer customer = findById();
        Optional<User> optUser = userRepository.findByEmailAndPassword(customer.getEmail(), customer.returnPassword());
        if (optUser.isPresent()) {
            userRepository.deleteById(optUser.get().getId());
        }
    }

    private void updateCustomerUser(Customer customer, Customer oldCustomer) {
        Optional<User> optUser = userRepository.findByEmailAndPassword(oldCustomer.getEmail(), oldCustomer.returnPassword());
        /*The user has to be present because we got here with token*/
        User user = optUser.get();
        user.setEmail(customer.getEmail());
        user.setPassword(customer.getPassword());
        userRepository.save(user);
    }

    /**
     * The password will be checked and if it is null the old password will stay,
     * the reason is that only the password can't be seen by findById because of json ignore.
     * On client to check what will be counted as null.
     * @param customer
     * @param oldCustomer
     */
    private void checkPassword(Customer customer, Customer oldCustomer) {
        if(customer.getPassword() == null){
            customer.setPassword(oldCustomer.getPassword());
        }
    }
}
