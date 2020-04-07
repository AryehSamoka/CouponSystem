package com.aryeh.CouponSystem.rest.controller;

import com.aryeh.CouponSystem.Service.CustomerServiceImpl;
import com.aryeh.CouponSystem.data.entity.ClientType;
import com.aryeh.CouponSystem.data.entity.Coupon;
import com.aryeh.CouponSystem.data.entity.Customer;
import com.aryeh.CouponSystem.rest.ClientSession;
import com.aryeh.CouponSystem.rest.ex.IllegalTokenException;
import com.aryeh.CouponSystem.rest.ex.InvalidTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/customer/{token}")
public class CustomerController {
    private Map<String, ClientSession> tokensMap;

    @Autowired
    public CustomerController(@Qualifier("tokens") Map<String, ClientSession> tokensMap) {
        this.tokensMap = tokensMap;
    }

    @GetMapping
    public ResponseEntity<Customer> getCustomerById(@PathVariable String token) {
        CustomerServiceImpl service = getService(token);
        Customer customer = service.findById();

        if (Customer.NO_ID == customer.getId()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(customer);
    }

    @PutMapping
    public ResponseEntity<Customer> updateCustomer(@PathVariable String token, @RequestBody Customer customer) {
        CustomerServiceImpl service = getService(token);
        return ResponseEntity.ok(service.update(customer));
    }

    @DeleteMapping
    public ResponseEntity<Customer> deleteCustomerById(@PathVariable String token) {
        CustomerServiceImpl service = getService(token);
        service.deleteById();
        synchronized (tokensMap) {
            tokensMap.remove(token);
        }

        return ResponseEntity.ok(Customer.empty());
    }

    @GetMapping("/all-other-coupons")
    public ResponseEntity<List<Coupon>> getAllOtherCoupons(@PathVariable String token){
        CustomerServiceImpl service = getService(token);
        return ResponseEntity.ok(service.findAllOtherCoupons());
    }

    @PostMapping("/coupon/{couponId}")
    public ResponseEntity<Customer> addCoupon(@PathVariable String token, @PathVariable long couponId) {
        CustomerServiceImpl service = getService(token);
        return ResponseEntity.ok(service.addCoupon(couponId));
    }

    @GetMapping("/coupons")
    ResponseEntity<List<Coupon>> findCustomerCoupons(@PathVariable String token) {
        CustomerServiceImpl service = getService(token);
        return ResponseEntity.ok(service.findCustomerCoupons());
    }

    @GetMapping("/coupons/category/{category}")
    ResponseEntity<List<Coupon>> findCustomerCouponsByCategory(@PathVariable String token, @PathVariable int category) {
        CustomerServiceImpl service = getService(token);
        return ResponseEntity.ok(service.findCustomerCouponsByCategory(category));
    }

    @GetMapping("/coupons/price/{price}")
    ResponseEntity<List<Coupon>> findCustomerCouponsLessThan(@PathVariable String token, @PathVariable double price) {
        CustomerServiceImpl service = getService(token);
        return ResponseEntity.ok(service.findCustomerCouponsLessThan(price));
    }

    @GetMapping("/emails-my-companies")
    ResponseEntity<List<String>> findEmailsMyCompanies(@PathVariable String token){
        CustomerServiceImpl service = getService(token);
        return ResponseEntity.ok(service.findEmailsMyCompanies());
    }

    private CustomerServiceImpl getService(String token) {
        ClientSession clientSession = tokensMap.get(token);
        if (null == clientSession) {
            throw new IllegalTokenException(String.format("your token: %s is illegal!", token));
        }else if(clientSession.getClientType() != ClientType.CUSTOMER){
            throw new InvalidTokenException(String.format("You aren't authorized as %s but as %s!",
                    ClientType.CUSTOMER, clientSession.getClientType()));
        }else {
            clientSession.accessed();
        }

        return (CustomerServiceImpl)clientSession.getService();
    }
}
