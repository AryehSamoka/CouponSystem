package com.aryeh.CouponSystem.Service;

import com.aryeh.CouponSystem.data.entity.*;
import com.aryeh.CouponSystem.data.repository.*;
import com.aryeh.CouponSystem.rest.ex.InvalidRootAdminAccessException;
import com.aryeh.CouponSystem.rest.ex.NoSuchCompanyException;
import com.aryeh.CouponSystem.rest.ex.NoSuchCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
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
    private UserRepository userRepository;
    private CompanyServiceImpl companyServiceImpl;
    private CustomerServiceImpl customerServiceImpl;
    private static final int ADMIN_ROLE = -1;


    @Autowired
    public AdminServiceImpl(CompanyRepository companyRepository, CouponRepository couponRepository,
                            CustomerRepository customerRepository, AdminRepository adminRepository,
                            Environment env, UserRepository userRepository, CompanyServiceImpl companyServiceImpl,
                            CustomerServiceImpl customerServiceImpl) {
        this.companyRepository = companyRepository;
        this.couponRepository = couponRepository;
        this.customerRepository = customerRepository;
        this.adminRepository = adminRepository;
        this.env = env;
        this.userRepository = userRepository;
        this.companyServiceImpl = companyServiceImpl;
        this.customerServiceImpl = customerServiceImpl;
    }

    @PostConstruct
    public void init() {
        Admin rootAdminDB = insertRootAdmin();
//
//        insertRootUser(rootAdminDB);
//
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

//                userRepository.save(new User(adminNew));

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
        Admin admin = findById();

        if (adminId != rootId) {
            Optional<User> optUser = userRepository.findByEmailAndPassword(admin.getEmail(), admin.returnPassword());

            if (optUser.isPresent()) {
                userRepository.deleteById(optUser.get().getId());
            }

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
            userRepository.save(new User(companyNew));
            return companyNew;
        }
        return Company.empty();
    }

    @Override
    @Transactional
    public Company updateCompany(Company company){
        companyServiceImpl.setCompanyId(company.getId());
        return companyServiceImpl.update(company);
    }

    @Override
    @Transactional
    public Company deleteCompanyById(long companyId) {
        deleteCompanyUser(checkCompany(companyId));
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

            userRepository.save(new User(customerNew));
            return customerNew;
        }
        return Customer.empty();
    }

    @Override
    @Transactional
    public Customer updateCustomer(Customer customer){
        customerServiceImpl.setCustomerId(customer.getId());
        return customerServiceImpl.update(customer);
    }

    @Override
    @Transactional
    public Customer deleteCustomerById(long customerId) {
        deleteCustomerUser(checkCustomer(customerId));
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

        Admin rootAdminDB;
        if (optionalAdmin.isPresent()) {
            rootAdmin.setId(optionalAdmin.get().getId());
        }
        rootAdminDB = adminRepository.save(rootAdmin);
        return rootAdminDB;
    }

    private void insertRootUser(Admin rootAdminDB) {
        User rootUser = new User(rootAdminDB);

        Optional<User> optionalRootUser = userRepository.findByEmailAndRole(rootAdminDB.getEmail(), ADMIN_ROLE);

        if (optionalRootUser.isPresent()) {
            rootUser.setId(optionalRootUser.get().getId());
        }
        userRepository.save(rootUser);
    }

    private Admin updateAdmin(Admin admin) {
        if (adminId != rootId) {

            Admin oldAdmin = findById();
            checkPassword(admin, oldAdmin);
            updateAdminUser(admin, oldAdmin);

            return adminRepository.save(admin);
        } else {
            throw new InvalidRootAdminAccessException("");
        }
    }

    private void updateAdminUser(Admin admin, Admin oldAdmin) {
        Optional<User> optUser = userRepository.findByEmailAndPassword(oldAdmin.getEmail(), oldAdmin.returnPassword());
        /*The user has to be present because we got here with token*/
        User user = optUser.get();
        user.setEmail(admin.getEmail());
        user.setPassword(admin.getPassword());
        userRepository.save(user);
    }

    /**
     * The password will be checked and if it is null the old password will stay,
     * the reason is that only the password can't be seen by findById because of json ignore.
     * On client to check what will be counted as null.
     * @param admin
     * @param oldAdmin
     */
    private void checkPassword(Admin admin, Admin oldAdmin) {
        if(admin.getPassword() == null){
            admin.setPassword(oldAdmin.getPassword());
        }
    }

    private Optional<Customer> checkCustomer(long customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (!optionalCustomer.isPresent()) {
            throw new NoSuchCustomerException("");
        }
        return optionalCustomer;
    }

    private void deleteCustomerUser(Optional<Customer> optionalCustomer) {
        Customer customer = optionalCustomer.get();
        Optional<User> optUser = userRepository.findByEmailAndPassword(customer.getEmail(), customer.returnPassword());

        if (optUser.isPresent()) {
            userRepository.deleteById(optUser.get().getId());
        }
    }

    private Optional<Company> checkCompany(long companyId) {
        Optional<Company> optionalCompany = companyRepository.findById(companyId);
        if (!optionalCompany.isPresent()) {

            throw new NoSuchCompanyException("");
        }
        return optionalCompany;
    }

    private void deleteCompanyUser(Optional<Company> optionalCompany) {
        Company company = optionalCompany.get();
        Optional<User> optUser = userRepository.findByEmailAndPassword(company.getEmail(), company.returnPassword());

        if (optUser.isPresent()) {
            userRepository.deleteById(optUser.get().getId());
        }
    }
}
