package com.aryeh.CouponSystem.rest.controller;

import com.aryeh.CouponSystem.Service.AbsService;
import com.aryeh.CouponSystem.Service.AdminServiceImpl;
import com.aryeh.CouponSystem.data.entity.Admin;
import com.aryeh.CouponSystem.data.entity.Company;
import com.aryeh.CouponSystem.data.entity.Customer;
import com.aryeh.CouponSystem.rest.ClientSession;
import com.aryeh.CouponSystem.rest.ex.InvalidLoginException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private Map<String, ClientSession> tokensMap;

    @Autowired
    public AdminController(@Qualifier("tokens") Map<String, ClientSession> tokensMap) {
        this.tokensMap = tokensMap;
    }

    @PostMapping("/{token}/admin")
    public ResponseEntity<Admin> saveAdmin(@PathVariable String token, @RequestBody Admin admin) {
        AdminServiceImpl service = getService(token);
        return ResponseEntity.ok(service.createAdmin(admin));
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

    @PutMapping("/{token}")
    public ResponseEntity<Admin> updateAdmin(@PathVariable String token, @RequestBody Admin admin) {
        AdminServiceImpl service = getService(token);
        return ResponseEntity.ok(service.update(admin));
    }

    @PostMapping("/{token}/company")
    public ResponseEntity<Company> saveCompany(@PathVariable String token, @RequestBody Company company) {
        AdminServiceImpl service = getService(token);
        return ResponseEntity.ok(service.createCompany(company));
    }

    @PutMapping("/{token}/company")
    public ResponseEntity<Company> updateCompany(@PathVariable String token, @RequestBody Company company){
        AdminServiceImpl service = getService(token);
        return ResponseEntity.ok(service.updateCompany(company));
    }

    @DeleteMapping("/{token}/{companyId}/company")
    public ResponseEntity<Company> deleteCompany(@PathVariable String token, @PathVariable long companyId) {
        AdminServiceImpl service = getService(token);

        return ResponseEntity.ok(service.deleteCompanyById(companyId));
    }

    @GetMapping("/{token}/all_companies")
    public ResponseEntity<List<Company>> getAllCompanies(@PathVariable String token){
        AdminServiceImpl service = getService(token);

        return ResponseEntity.ok(service.findAllCompanies());
    }

    @PostMapping("/{token}/customer")
    public ResponseEntity<Customer> saveCustomer(@PathVariable String token, @RequestBody Customer customer) {
        AdminServiceImpl service = getService(token);
        return ResponseEntity.ok(service.createCustomer(customer));
    }

    @PutMapping("/{token}/customer")
    public ResponseEntity<Customer> updateCustomer(@PathVariable String token, @RequestBody Customer customer){
        AdminServiceImpl service = getService(token);
        return ResponseEntity.ok(service.updateCustomer(customer));
    }

    @DeleteMapping("/{token}/{customerId}/customer")
    public ResponseEntity<Customer> deleteCustomer(@PathVariable String token, @PathVariable long customerId) {
        AdminServiceImpl service = getService(token);

        return ResponseEntity.ok(service.deleteCustomerById(customerId));
    }

    @GetMapping("/{token}/all_customers")
    public ResponseEntity<List<Customer>> getAllCustomers(@PathVariable String token){
        AdminServiceImpl service = getService(token);

        return ResponseEntity.ok(service.findAllCustomers());
    }

    @GetMapping("/{token}/all_categories")
    public ResponseEntity<List<Integer>> getAllCategories(@PathVariable String token){
        AdminServiceImpl service = getService(token);

        return ResponseEntity.ok(service.findAllCategories());
    }

    private AdminServiceImpl getService(String token) {
        ClientSession clientSession = tokensMap.get(token);
        if (null == clientSession) {
            throw new InvalidLoginException("You aren't authorized");
        }else {
            clientSession.accessed();
        }

        AbsService absService = clientSession.getService();

        if (!(absService instanceof AdminServiceImpl)) {
            throw new InvalidLoginException("You aren't authorized");
        }
        AdminServiceImpl service = (AdminServiceImpl)absService;
        return service;
    }
}
