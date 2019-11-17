package com.aryeh.CouponSystem.Service;

import com.aryeh.CouponSystem.data.entity.*;
import com.aryeh.CouponSystem.data.repository.*;
import com.aryeh.CouponSystem.rest.ex.InvalidRootAdminAccessException;
import org.hibernate.TransientObjectException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl extends AbsService implements AdminService {
    private long rootId;
    private final CompanyRepository companyRepository;
    private final CouponRepository couponRepository;
    private final CustomerRepository customerRepository;
    private AdminRepository adminRepository;
    private Environment env;
    private UserRepository userRepository;


    @Autowired
    public AdminServiceImpl(CompanyRepository companyRepository, CouponRepository couponRepository,
                            CustomerRepository customerRepository, AdminRepository adminRepository, Environment env, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.couponRepository = couponRepository;
        this.customerRepository = customerRepository;
        this.adminRepository = adminRepository;
        this.env = env;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        String rootUsername = env.getProperty("adminRoot.username");
        String rootPassword = env.getProperty("adminRoot.password");
        Admin rootAdmin = new Admin(rootUsername, rootPassword);
        try {
            adminRepository.save(rootAdmin);
        } catch (DataIntegrityViolationException e) {

        }

        User rootUser = new User(rootAdmin);

        try {
            userRepository.save(rootUser);
        } catch (InvalidDataAccessApiUsageException e) {

        }

        Optional<User> rootUserDB = userRepository.findByEmailAndPassword(rootAdmin.getEmail(), rootAdmin.getPassword());
        rootId = rootUserDB.get().getClient().getId();
        System.out.println(rootId);
    }

    @Override
    @Transactional
    public Admin createAdmin(Admin admin) {
        if (admin != null) {
            /*It isn't possible to change the root administrator from here.*/
            if (admin.getId() != rootId) {

                admin.setId(0);
                Admin adminNew = adminRepository.save(admin);

                userRepository.save(new User(adminNew));

                return adminNew;
            } else {
                throw new InvalidRootAdminAccessException("");
            }
        }
        return Admin.empty();
    }

    @Override
    @Transactional
    public Company createCompany(Company company) {
        if (company != null) {

            company.setId(0);
            /*A company can be created with his own coupons but can't update coupons of other companies.*/
            List<Coupon> coupons = company.getCoupons();
            for (Coupon coupon : coupons) {
                coupon.setId(0);
            }

            Company companyNew = companyRepository.save(company);
            userRepository.save(new User(companyNew));
            return companyNew;
        }
        return Company.empty();
    }

    @Override
    @Transactional
    public Customer createCustomer(Customer customer) {
        if (customer != null) {

            customer.setId(0);
            /*A customer can't be created with coupons.*/
            customer.setCoupons(Collections.emptyList());

            Customer customerNew = customerRepository.save(customer);

            userRepository.save(new User(customerNew));
            return customerNew;
        }
        return Customer.empty();
    }

}
