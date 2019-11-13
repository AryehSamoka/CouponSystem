package com.aryeh.CouponSystem.rest.controller;

import com.aryeh.CouponSystem.Service.AbsService;
import com.aryeh.CouponSystem.Service.CustomerService;
import com.aryeh.CouponSystem.Service.CustomerServiceImpl;
import com.aryeh.CouponSystem.data.entity.Coupon;
import com.aryeh.CouponSystem.data.entity.Customer;
import com.aryeh.CouponSystem.rest.AbsSession;
import com.aryeh.CouponSystem.rest.ClientSession;
import com.aryeh.CouponSystem.rest.CustomerSession;
import com.aryeh.CouponSystem.rest.ex.InvalidLoginException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    private Map<String, ClientSession> tokensMap;

    @Autowired
    public CustomerController(@Qualifier("tokens") Map<String, ClientSession> tokensMap) {
        this.tokensMap = tokensMap;
    }

    @GetMapping("/{token}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable String token) {
        CustomerServiceImpl service = getService(token);
        Customer customer = service.findById();

        if (Customer.NO_ID == customer.getId()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(customer);
    }

    @DeleteMapping("/{token}")
    public ResponseEntity<Customer> deleteCustomerById(@PathVariable String token) {
        CustomerServiceImpl service = getService(token);
        service.deleteById();
        return ResponseEntity.ok(Customer.empty());
    }

    @PutMapping("/{token}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable String token, @RequestBody Customer customer) {
        CustomerServiceImpl service = getService(token);
        return ResponseEntity.ok(service.update(customer));
    }

    @GetMapping("/{token}/coupons")
    ResponseEntity<List<Coupon>> findCustomerCoupons(@PathVariable String token) {
        CustomerServiceImpl service = getService(token);
        return ResponseEntity.ok(service.findCustomerCoupons());
    }

    @GetMapping("/{token}/coupons/category/{category}")
    ResponseEntity<List<Coupon>> findCustomerCouponsByCategory(@PathVariable String token, @PathVariable int category) {
        CustomerServiceImpl service = getService(token);
        return ResponseEntity.ok(service.findCustomerCouponsByCategory(category));
    }

    @GetMapping("/{token}/coupons/price/{price}")
    ResponseEntity<List<Coupon>> findCustomerCouponsLessThan(@PathVariable String token, @PathVariable double price) {
        CustomerServiceImpl service = getService(token);
        return ResponseEntity.ok(service.findCustomerCouponsLessThan(price));
    }

    private CustomerServiceImpl getService(String token) {
        ClientSession clientSession = tokensMap.get(token);
        if (null == clientSession) {
            throw new InvalidLoginException("You aren't authorized");
        }else {
            clientSession.accessed();
        }

        AbsService absService = clientSession.getService();

        if (!(absService instanceof CustomerServiceImpl)) {
            throw new InvalidLoginException("You aren't authorized");
        }
        CustomerServiceImpl service = (CustomerServiceImpl)absService;
        return service;
    }
}
