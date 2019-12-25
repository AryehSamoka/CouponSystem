package com.aryeh.CouponSystem.rest.controller;

import com.aryeh.CouponSystem.Service.CompanyServiceImpl;
import com.aryeh.CouponSystem.data.entity.ClientType;
import com.aryeh.CouponSystem.data.entity.Company;
import com.aryeh.CouponSystem.data.entity.Coupon;
import com.aryeh.CouponSystem.rest.ClientSession;
import com.aryeh.CouponSystem.rest.ex.IllegalTokenException;
import com.aryeh.CouponSystem.rest.ex.InvalidTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/company/{token}")
public class CompanyController {
    private Map<String, ClientSession> tokensMap;

    @Autowired
    public CompanyController(@Qualifier("tokens") Map<String, ClientSession> tokensMap) {
        this.tokensMap = tokensMap;
    }

    @GetMapping
    public ResponseEntity<Company> getCompanyByToken(@PathVariable String token) {
        CompanyServiceImpl service = getService(token);
        Company company = service.findById();
        return ResponseEntity.ok(company);
    }

    @PutMapping
    public ResponseEntity<Company> updateCompany(@PathVariable String token, @RequestBody Company company) {
        CompanyServiceImpl service = getService(token);
        return ResponseEntity.ok(service.update(company));
    }

    @DeleteMapping
    public ResponseEntity<Company> deleteCompanyByToken(@PathVariable String token) {
        CompanyServiceImpl service = getService(token);
        service.deleteById();
        synchronized (tokensMap) {
            tokensMap.remove(token);
        }

        return ResponseEntity.ok(Company.empty());
    }

    @PostMapping("/coupon")
    public ResponseEntity<Coupon> addCoupon(@PathVariable String token, @RequestBody Coupon coupon) {
        CompanyServiceImpl service = getService(token);
        return ResponseEntity.ok(service.addCoupon(coupon));
    }

    @PostMapping("/coupons")
    public ResponseEntity<Company> addCoupons(@PathVariable String token, @RequestBody List<Coupon> coupons) {
        CompanyServiceImpl service = getService(token);
        return ResponseEntity.ok(service.addCoupons(coupons));
    }

    @PutMapping("/coupon")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable String token, @RequestBody Coupon coupon) {
        CompanyServiceImpl service = getService(token);
        return ResponseEntity.ok(service.updateCoupon(coupon));
    }

    @DeleteMapping("/coupon/{couponId}")
    public ResponseEntity<Coupon> deleteCouponById(@PathVariable String token, @PathVariable long couponId) {
        CompanyServiceImpl service = getService(token);
        service.deleteCoupon(couponId);

        return ResponseEntity.ok(Coupon.empty());
    }

    @GetMapping("/coupons")
    ResponseEntity<List<Coupon>> findCompanyCoupons(@PathVariable String token) {
        CompanyServiceImpl service = getService(token);
        return ResponseEntity.ok(service.findCompanyCoupons());
    }

    @GetMapping("/coupons/category/{category}")
    ResponseEntity<List<Coupon>> findCompanyCouponsByCategory(@PathVariable String token, @PathVariable int category) {
        CompanyServiceImpl service = getService(token);

        return ResponseEntity.ok(service.findCompanyCouponsByCategory(category));
    }

    @GetMapping("/coupons/price/{price}")
    ResponseEntity<List<Coupon>> findCompanyCouponsLessThan(@PathVariable String token, @PathVariable double price) {
        CompanyServiceImpl service = getService(token);
        return ResponseEntity.ok(service.findCompanyCouponsLessThan(price));
    }

    @GetMapping("/coupons/end-date/{date}")
    ResponseEntity<List<Coupon>> findCompanyCouponsBeforeDate(@PathVariable String token, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        CompanyServiceImpl service = getService(token);
        return ResponseEntity.ok(service.findCompanyCouponsBeforeDate(date));
    }

    @GetMapping("/emails-my-customers")
    ResponseEntity<List<String>> findEmailsMyCustomers(@PathVariable String token){
        CompanyServiceImpl service = getService(token);
        return ResponseEntity.ok(service.findEmailsMyCustomers());
    }

    private CompanyServiceImpl getService(String token) {
        ClientSession clientSession = tokensMap.get(token);
        if (null == clientSession) {
            throw new IllegalTokenException(String.format("your token: %s is illegal!", token));
        }else if(clientSession.getClientType() != ClientType.COMPANY){
            throw new InvalidTokenException(String.format("You aren't authorized as %s but as %s!",
                    ClientType.COMPANY, clientSession.getClientType()));
        }else {
            clientSession.accessed();
        }

        return (CompanyServiceImpl) clientSession.getService();
    }
}
