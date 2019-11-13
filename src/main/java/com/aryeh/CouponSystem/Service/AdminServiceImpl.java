package com.aryeh.CouponSystem.Service;

import com.aryeh.CouponSystem.data.entity.*;
import com.aryeh.CouponSystem.data.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class AdminServiceImpl extends AbsService implements AdminService {
    private final long rootId;
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
        rootId = Long.parseLong(env.getProperty("adminRoot.id"));
    }

    @PostConstruct
    public void init() {
        String rootUsername = env.getProperty("adminRoot.username");
        String rootPassword = env.getProperty("adminRoot.password");
        adminRepository.save((new Admin(rootId, rootUsername, rootPassword)));
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
               return Admin.empty();
            }
        }
        return Admin.empty();
    }

    @Override
    @Transactional
    public Company createCompany(Company company) {
        if (company != null) {

            company.setId(0);
            /*A company can be created with coupons but can't update other coupons.*/
            List<Coupon> coupons = company.getCoupons();
            for (Coupon coupon:coupons) {
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
            /*A customer can't be crated with coupons.*/
            customer.setCoupons(null);

            Customer customerNew = customerRepository.save(customer);

            userRepository.save(new User(customerNew));
            return customerNew;
        }
        return Customer.empty();
    }

}
