package com.aryeh.CouponSystem.Service;

import com.aryeh.CouponSystem.data.entity.Admin;
import com.aryeh.CouponSystem.data.entity.Company;
import com.aryeh.CouponSystem.data.entity.Coupon;
import com.aryeh.CouponSystem.data.entity.Customer;
import com.aryeh.CouponSystem.data.repository.AdminRepository;
import com.aryeh.CouponSystem.data.repository.CompanyRepository;
import com.aryeh.CouponSystem.data.repository.CouponRepository;
import com.aryeh.CouponSystem.data.repository.CustomerRepository;
import com.aryeh.CouponSystem.rest.ex.InvalidRootAdminAccessException;
import com.aryeh.CouponSystem.rest.ex.NoSuchCompanyException;
import com.aryeh.CouponSystem.rest.ex.NoSuchCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl extends AbsService implements AdminService {
    private long adminId;
    private long rootId;
    private final CompanyRepository companyRepository;
    private final CouponRepository couponRepository;
    private final CustomerRepository customerRepository;
    private AdminRepository adminRepository;
    private Environment env;
    private ApplicationContext context;


    @Autowired
    public AdminServiceImpl(CompanyRepository companyRepository, CouponRepository couponRepository,
                            CustomerRepository customerRepository, AdminRepository adminRepository,
                            Environment env, ApplicationContext context) {
        this.companyRepository = companyRepository;
        this.couponRepository = couponRepository;
        this.customerRepository = customerRepository;
        this.adminRepository = adminRepository;
        this.env = env;
        this.context = context;
    }

    @PostConstruct
    public void init() {
        Admin rootAdminDB = insertRootAdmin();

        rootId = rootAdminDB.getId();
    }

    public long getAdminId() {
        return adminId;
    }

    public void setAdminId(long adminId) {
        this.adminId = adminId;
    }

    @Override
    @Transactional
    public Admin createAdmin(Admin admin) {
        if (admin != null) {
            /*It isn't possible to change the root administrator from here.*/
            if (admin.getId() != rootId) {

                admin.setId(0);
                Admin adminNew = adminRepository.save(admin);
                return adminNew;
            } else {
                throw new InvalidRootAdminAccessException("");
            }
        }
        return Admin.empty();
    }

    @Override
    @Transactional
    public Admin findById() {
        return adminRepository.findById(adminId)
                .orElse(Admin.empty());
    }

    @Override
    @Transactional
    public void deleteById() {
        if (adminId != rootId) {
            adminRepository.deleteById(adminId);
        } else {
            throw new InvalidRootAdminAccessException("");
        }
    }

    @Override
    @Transactional
    public Admin update(Admin admin) {
        if (admin.getId() == adminId || admin.getId() == 0) {
            admin.setId(adminId);
            return updateAdmin(admin);
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
            return companyNew;
        }
        return Company.empty();
    }

    @Override
    @Transactional
    public Company updateCompany(Company company) {
        CompanyServiceImpl companyServiceImpl = context.getBean(CompanyServiceImpl.class);
        companyServiceImpl.setCompanyId(company.getId());
        return companyServiceImpl.update(company);
    }

    @Override
    @Transactional
    public Company deleteCompanyById(long companyId) {
        checkCompany(companyId);
        companyRepository.deleteById(companyId);
        return Company.empty();
    }

    @Override
    @Transactional
    public List<Company> findAllCompanies() {
        return companyRepository.findAll();
    }

    @Override
    @Transactional
    public List<Company> findAllCompaniesWithoutCoupons() {
        return companyRepository.findAllCompaniesWithoutCoupons();
    }

    @Override
    @Transactional
    public Customer createCustomer(Customer customer) {
        if (customer != null) {
            customer.setId(0);

            /*A customer can't be created with coupons.*/
            customer.setCoupons(Collections.emptyList());

            Customer customerNew = customerRepository.save(customer);
            return customerNew;
        }
        return Customer.empty();
    }

    @Override
    @Transactional
    public Customer updateCustomer(Customer customer) {
        CustomerServiceImpl customerServiceImpl = context.getBean(CustomerServiceImpl.class);
        customerServiceImpl.setCustomerId(customer.getId());
        return customerServiceImpl.update(customer);
    }

    @Override
    @Transactional
    public Customer deleteCustomerById(long customerId) {
        checkCustomer(customerId);
        customerRepository.deleteById(customerId);
        return Customer.empty();
    }

    @Override
    @Transactional
    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    @Transactional
    public List<Customer> findAllCustomersWithoutCoupons() {
        return customerRepository.findAllCustomersWithoutCoupons();
    }

    @Override
    @Transactional
    public List<Integer> findAllCategories() {
        return couponRepository.findAllCategories();
    }

    @Override
    @Transactional
    public List<String> getEmailsCompsAndCustoms() {
        List<String> allEmails = new ArrayList<>();
        allEmails.addAll(companyRepository.findAllEmails());
        allEmails.addAll(customerRepository.findAllEmails());
        return allEmails;
    }

    private Admin insertRootAdmin() {
        Admin rootAdmin = new Admin(env.getProperty("adminRoot.username"), env.getProperty("adminRoot.password"));
        Optional<Admin> optionalAdmin = adminRepository.findByEmail(rootAdmin.getEmail());

        if (optionalAdmin.isPresent()) {
            rootAdmin.setId(optionalAdmin.get().getId());
        }
        return adminRepository.save(rootAdmin);
    }

    private Admin updateAdmin(Admin admin) {
        if (adminId != rootId) {
            admin.checkPassword(findById());
            return adminRepository.save(admin);
        } else {
            throw new InvalidRootAdminAccessException("");
        }
    }

    private void checkCustomer(long customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (!optionalCustomer.isPresent()) {
            throw new NoSuchCustomerException("");
        }
    }

    private void checkCompany(long companyId) {
        Optional<Company> optionalCompany = companyRepository.findById(companyId);
        if (!optionalCompany.isPresent()) {

            throw new NoSuchCompanyException("");
        }
    }
}
