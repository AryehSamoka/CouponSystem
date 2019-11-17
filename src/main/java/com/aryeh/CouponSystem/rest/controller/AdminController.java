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

    @PostMapping("/{token}/customer")
    public ResponseEntity<Customer> saveCustomer(@PathVariable String token, @RequestBody Customer customer) {
        AdminServiceImpl service = getService(token);
        return ResponseEntity.ok(service.createCustomer(customer));
    }

    @PostMapping("/{token}/company")
    public ResponseEntity<Company> saveCompany(@PathVariable String token, @RequestBody Company company) {
        AdminServiceImpl service = getService(token);
        return ResponseEntity.ok(service.createCompany(company));
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
