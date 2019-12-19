package com.aryeh.CouponSystem.Service;

import com.aryeh.CouponSystem.data.entity.*;
import com.aryeh.CouponSystem.data.repository.*;
import com.aryeh.CouponSystem.rest.ex.InvalidRootAdminAccessException;
import com.aryeh.CouponSystem.rest.ex.NoSuchCompanyException;
import com.aryeh.CouponSystem.rest.ex.NoSuchCustomerException;
import com.aryeh.CouponSystem.rest.ex.invalidIdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Service
public class AdminServiceImpl extends AbsService implements AdminService {
    private static final int MIN_COUPONS_AMOUNT = 30;
    private long clientId;
    private long rootId;
    private ClientRepository clientRepository;
    private final CompanyRepository companyRepository;
    private final CouponRepository couponRepository;
    private final CustomerRepository customerRepository;
    private AdminRepository adminRepository;
    private Environment env;
    private ApplicationContext context;


    @Autowired
    public AdminServiceImpl(ClientRepository clientRepository, CompanyRepository companyRepository, CouponRepository couponRepository,
                            CustomerRepository customerRepository, AdminRepository adminRepository,
                            Environment env, ApplicationContext context) {
        this.clientRepository = clientRepository;
        this.companyRepository = companyRepository;
        this.couponRepository = couponRepository;
        this.customerRepository = customerRepository;
        this.adminRepository = adminRepository;
        this.env = env;
        this.context = context;
    }

    @PostConstruct
    public void init() {
        insertRootAdmin();
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
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
        return adminRepository.findById(clientId)
                .orElse(Admin.empty());
    }

    @Override
    @Transactional
    public void deleteById() {
        if (clientId != rootId) {
            adminRepository.deleteById(clientId);
        } else {
            throw new InvalidRootAdminAccessException("");
        }
    }

    @Override
    @Transactional
    public Admin update(Admin admin) {
        if (admin.getId() == clientId || admin.getId() == 0) {
            admin.setId(clientId);
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
            Set<Coupon> coupons = company.getCoupons();
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
    public Company getCompanyById(long companyId) {
        CompanyServiceImpl companyServiceImpl = context.getBean(CompanyServiceImpl.class);
        companyServiceImpl.setClientId(companyId);
        return companyServiceImpl.findById();
    }

    @Override
    @Transactional
    public Company updateCompany(Company company) {
        final Optional<Client> optionalClient = clientRepository.findById(company.getId());
        if(!optionalClient.isPresent()){
            throw new invalidIdException("Invalid id: " + company.getId());
        }
        if(!(optionalClient.get() instanceof Company)){
            throw new NoSuchCompanyException("");
        }
        if(company.getId() == 0){
            throw new invalidIdException("Invalid updating without id");
        }
        CompanyServiceImpl companyServiceImpl = context.getBean(CompanyServiceImpl.class);
        companyServiceImpl.setClientId(company.getId());
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
            customer.setCoupons(Collections.emptySet());

            Customer customerNew = customerRepository.save(customer);
            return customerNew;
        }
        return Customer.empty();
    }

    @Override
    @Transactional
    public Customer updateCustomer(Customer customer) {
        final Optional<Client> optionalClient = clientRepository.findById(customer.getId());
        if(!optionalClient.isPresent()){
            throw new invalidIdException("Invalid id: " + customer.getId());
        }
        if(!(optionalClient.get() instanceof Customer)){
            throw new NoSuchCustomerException("");
        }
        CustomerServiceImpl customerServiceImpl = context.getBean(CustomerServiceImpl.class);
        customerServiceImpl.setClientId(customer.getId());
        return customerServiceImpl.update(customer);
    }

    @Override
    public Customer getCustomerById(long customerId) {
        CustomerServiceImpl service = context.getBean(CustomerServiceImpl.class);
        service.setClientId(customerId);
        return service.findById();
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

    public List<String[]> findPairsEmailsOfCompsCustomersOrderedByCategory(){
        return adminRepository.findPairsEmailsOfCompsCustomersOrderedByCategory();
    }

    public List<Integer[]> CountPairsByCategory(){
        return adminRepository.CountPairsByCategory();
    }

    @Override
    @Transactional
    public void InsertRandomValuesToDB() {
        List<Long> couponIds = new ArrayList<>();
        for (AtomicInteger i = new AtomicInteger(); i.get() < 50; i.getAndIncrement()) {
            Company company = randomCompany();
            Company newCompany = companyRepository.save(company);

            int coupons = (int) (Math.random() * (4 + 1));
            for (AtomicInteger j = new AtomicInteger(); j.get() < coupons; j.getAndIncrement()) {
                Coupon coupon = randomCoupon(newCompany);
                couponIds.add(couponRepository.save(coupon).getId());
            }
        }

        for (AtomicInteger i = new AtomicInteger(); i.get() < 50; i.getAndIncrement()) {
            Customer customer = randomCustomer();
            Customer newCustomer = customerRepository.save(customer);

            insertCustomerCoupon(couponIds, newCustomer.getId());
        }
    }


    private void insertRootAdmin() {
        Admin rootAdmin = new Admin(env.getProperty("adminRoot.username"), env.getProperty("adminRoot.password"));
        Optional<Admin> optionalAdmin = adminRepository.findByEmail(rootAdmin.getEmail());

        if (optionalAdmin.isPresent()) {
            rootAdmin.setId(optionalAdmin.get().getId());
        }

        Admin rootAdminDB = adminRepository.save(rootAdmin);
        rootId = rootAdminDB.getId();
    }

    private Admin updateAdmin(Admin admin) {
        if (clientId != rootId) {
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

    private void insertCustomerCoupon(List<Long> couponIds, long customerId) {
        CustomerServiceImpl service = context.getBean(CustomerServiceImpl.class);
        service.setClientId(customerId);
        int index = (int) (Math.random() * (10 + 1));
        Collections.shuffle(couponIds);
        IntStream.range(0, index).mapToLong(couponIds::get).forEach(service::addCoupon);
    }

    private Company randomCompany() {
        String name = UUID.randomUUID().toString().substring(0, 5);
        String email = name + "@gmail.com";
        String password = UUID.randomUUID().toString().substring(0, 7);

        return new Company(name, email, password);
    }

    private static Coupon randomCoupon(Company company) {
        int category = 1 + (int) (Math.random() * (9 + 1));
        String title = UUID.randomUUID().toString().substring(0, 5);
        LocalDate startDate = LocalDate.now();
        int addDays = 1 + (int) (Math.random() * (99 + 1));
        LocalDate endDate = startDate.plusDays(addDays);
        int amount = MIN_COUPONS_AMOUNT + (int) (Math.random() * (100 + 1));
        String description = title + ", " + UUID.randomUUID().toString().substring(0, 5);
        double price = 10 + (int) (Math.random() * (990 + 1));
        String image = title + "@image.com";

        return new Coupon(company, title, startDate, endDate, category, amount, description, price, image);
    }

    private static Customer randomCustomer() {
        String firstName = UUID.randomUUID().toString().substring(0, 3);
        String lastName = UUID.randomUUID().toString().substring(0, 3);
        String email = firstName + "." + lastName + "@gmail.com";
        String password = UUID.randomUUID().toString().substring(0, 7);

        return new Customer(firstName, lastName, email, password);
    }
}
