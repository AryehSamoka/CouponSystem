package com.aryeh.CouponSystem.rest.controller;

import com.aryeh.CouponSystem.Service.AdminServiceImpl;
import com.aryeh.CouponSystem.data.entity.Admin;
import com.aryeh.CouponSystem.data.entity.ClientType;
import com.aryeh.CouponSystem.data.entity.Company;
import com.aryeh.CouponSystem.data.entity.Customer;
import com.aryeh.CouponSystem.rest.ClientSession;
import com.aryeh.CouponSystem.rest.ex.IllegalTokenException;
import com.aryeh.CouponSystem.rest.ex.InvalidTokenException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController implements ApplicationContextAware {
    private static final Map<String, String> SHUTDOWN_MESSAGE = Collections
            .unmodifiableMap(Collections.singletonMap("message", "Shutting down, bye..."));
    private static final long HALF_SECOND_MILLIS = 500;
    private Map<String, ClientSession> tokensMap;
    private ConfigurableApplicationContext context;

    @Autowired
    public AdminController(@Qualifier("tokens") Map<String, ClientSession> tokensMap) {
        this.tokensMap = tokensMap;
    }

    @PostMapping("/{token}")
    public ResponseEntity<Admin> saveAdmin(@PathVariable String token, @RequestBody Admin admin) {
        AdminServiceImpl service = getService(token);
        return ResponseEntity.ok(service.createAdmin(admin));
    }

    @GetMapping("/{token}")
    public ResponseEntity<Admin> findAdmin(@PathVariable String token) {
        AdminServiceImpl service = getService(token);
        return ResponseEntity.ok(service.findById());
    }

    @PutMapping("/{token}")
    public ResponseEntity<Admin> updateAdmin(@PathVariable String token, @RequestBody Admin admin) {
        AdminServiceImpl service = getService(token);
        return ResponseEntity.ok(service.update(admin));
    }

    @DeleteMapping("/{token}")
    public ResponseEntity<Admin> deleteAdminByToken(@PathVariable String token) {
        AdminServiceImpl service = getService(token);
        service.deleteById();
        synchronized (tokensMap) {
            tokensMap.remove(token);
        }

        return ResponseEntity.ok(Admin.empty());
    }

    @PostMapping("/{token}/close_application_by_root_admin")
    public ResponseEntity<Map<String, String>> closeApplicationByRootAdmin(@PathVariable String token) {
        final AdminServiceImpl service = getService(token);
        service.checkRootAdmin();
        try {
            return ResponseEntity.ok(SHUTDOWN_MESSAGE);
        }finally {
            Thread thread = new Thread(this::performShutdown);
            thread.setContextClassLoader(getClass().getClassLoader());
            thread.start();
        }
    }

    private void performShutdown() {
        try {
            Thread.sleep(HALF_SECOND_MILLIS);
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        this.context.close();
    }

    @PostMapping("/{token}/company")
    public ResponseEntity<Company> saveCompany(@PathVariable String token, @RequestBody Company company) {
        AdminServiceImpl service = getService(token);
        return ResponseEntity.ok(service.createCompany(company));
    }

    @GetMapping("/{token}/{companyId}/company")
    public ResponseEntity<Company> getCompany(@PathVariable String token,  @PathVariable long companyId) {
        AdminServiceImpl service = getService(token);
        return ResponseEntity.ok(service.getCompanyById(companyId));
    }

    @PutMapping("/{token}/company")
    public ResponseEntity<Company> updateCompany(@PathVariable String token, @RequestBody Company company) {
        AdminServiceImpl service = getService(token);
        return ResponseEntity.ok(service.updateCompany(company));
    }

    @DeleteMapping("/{token}/{companyId}/company")
    public ResponseEntity<Company> deleteCompany(@PathVariable String token, @PathVariable long companyId) {
        AdminServiceImpl service = getService(token);

        return ResponseEntity.ok(service.deleteCompanyById(companyId));
    }

    @GetMapping("/{token}/all_companies")
    public ResponseEntity<List<Company>> getAllCompanies(@PathVariable String token) {
        AdminServiceImpl service = getService(token);

        return ResponseEntity.ok(service.findAllCompanies());
    }

    @GetMapping("/{token}/all_companies_without_coupons")
    public ResponseEntity<List<Company>> getAllCompaniesWithoutCoupons(@PathVariable String token) {
        AdminServiceImpl service = getService(token);

        return ResponseEntity.ok(service.findAllCompaniesWithoutCoupons());
    }

    @PostMapping("/{token}/customer")
    public ResponseEntity<Customer> saveCustomer(@PathVariable String token, @RequestBody Customer customer) {
        AdminServiceImpl service = getService(token);
        return ResponseEntity.ok(service.createCustomer(customer));
    }

    @GetMapping("/{token}/{customerId}/customer")
    public ResponseEntity<Customer> getCustomer(@PathVariable String token,  @PathVariable long customerId) {
        AdminServiceImpl service = getService(token);
        return ResponseEntity.ok(service.getCustomerById(customerId));
    }

    @PutMapping("/{token}/customer")
    public ResponseEntity<Customer> updateCustomer(@PathVariable String token, @RequestBody Customer customer) {
        AdminServiceImpl service = getService(token);
        return ResponseEntity.ok(service.updateCustomer(customer));
    }

    @DeleteMapping("/{token}/{customerId}/customer")
    public ResponseEntity<Customer> deleteCustomer(@PathVariable String token, @PathVariable long customerId) {
        AdminServiceImpl service = getService(token);

        return ResponseEntity.ok(service.deleteCustomerById(customerId));
    }

    @GetMapping("/{token}/all_customers")
    public ResponseEntity<List<Customer>> getAllCustomers(@PathVariable String token) {
        AdminServiceImpl service = getService(token);

        return ResponseEntity.ok(service.findAllCustomers());
    }

    @GetMapping("/{token}/all_customers_without_coupons")
    public ResponseEntity<List<Customer>> getAllCustomersWithoutCoupons(@PathVariable String token) {
        AdminServiceImpl service = getService(token);

        return ResponseEntity.ok(service.findAllCustomersWithoutCoupons());
    }

    @GetMapping("/{token}/all_categories")
    public ResponseEntity<List<Integer>> getAllCategories(@PathVariable String token) {
        AdminServiceImpl service = getService(token);

        return ResponseEntity.ok(service.findAllCategories());
    }

    @GetMapping("/{token}/all_emails_comps_customs")
    public ResponseEntity<List<String>> getEmailsCompsAndCustoms(@PathVariable String token) {
        AdminServiceImpl service = getService(token);

        return ResponseEntity.ok(service.getEmailsCompsAndCustoms());
    }

    @GetMapping("/{token}/pairs_emails_comps_customs_ordered_by_category")
    public ResponseEntity<List<String[]>> findPairsEmailsOfCompsCustomersOrderedByCategory(@PathVariable String token) {
        AdminServiceImpl service = getService(token);

        return ResponseEntity.ok(service.findPairsEmailsOfCompsCustomersOrderedByCategory());
    }

    /**will count pairs of companies and customers with same category and order them descendingly
     * by this count.
     *
     * @param token
     * @return
     */
    @GetMapping("/{token}/count_pairs_by_category")
    public ResponseEntity<List<Integer[]>> CountPairsByCategory(@PathVariable String token) {
        AdminServiceImpl service = getService(token);

        return ResponseEntity.ok(service.CountPairsByCategory());
    }

    @GetMapping("/{token}/{email}/password_by_email")
    public ResponseEntity<String> findPasswordByEmail(@PathVariable String token, @PathVariable String email) {
        AdminServiceImpl service = getService(token);

        return ResponseEntity.ok(service.findPasswordByEmail(email));
    }

    @PostMapping("/{token}/insert_random_values_to_DB")
    public ResponseEntity<String> InsertRandomValuesToDB(@PathVariable String token) {
        AdminServiceImpl service = getService(token);
        service.InsertRandomValuesToDB();
        return ResponseEntity.ok("succeeded");
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        if (context instanceof ConfigurableApplicationContext) {
            this.context = (ConfigurableApplicationContext) context;
        }
    }

    private AdminServiceImpl getService(String token) {
        ClientSession clientSession = tokensMap.get(token);
        if (null == clientSession) {
            throw new IllegalTokenException(String.format("your token: %s is illegal!", token));
        }else if(clientSession.getClientType() != ClientType.ADMIN){
            throw new InvalidTokenException(String.format("You aren't authorized as %s but as %s!",
                    ClientType.ADMIN, clientSession.getClientType()));
        }else {
            clientSession.accessed();
        }
        return (AdminServiceImpl)clientSession.getService();
    }
}
