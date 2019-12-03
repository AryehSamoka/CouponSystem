package com.aryeh.CouponSystem.rest.controller;

import com.aryeh.CouponSystem.Service.AbsService;
import com.aryeh.CouponSystem.Service.CompanyServiceImpl;
import com.aryeh.CouponSystem.data.entity.Company;
import com.aryeh.CouponSystem.data.entity.Coupon;
import com.aryeh.CouponSystem.data.entity.Customer;
import com.aryeh.CouponSystem.rest.ClientSession;
import com.aryeh.CouponSystem.rest.ex.InvalidLoginException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/company")
public class CompanyController {
    private Map<String, ClientSession> tokensMap;

    @Autowired
    public CompanyController(@Qualifier("tokens") Map<String, ClientSession> tokensMap) {
        this.tokensMap = tokensMap;
    }

    @PostMapping("/{token}/coupon")
    public ResponseEntity<Coupon> addCoupon(@PathVariable String token, @RequestBody Coupon coupon) {
        CompanyServiceImpl service = getService(token);
        return ResponseEntity.ok(service.addCoupon(coupon));
    }

    @PostMapping("/{token}/coupons")
    public ResponseEntity<Company> addCoupons(@PathVariable String token, @RequestBody List<Coupon> coupons) {
        CompanyServiceImpl service = getService(token);
        return ResponseEntity.ok(service.addCoupons(coupons));
    }

    @PutMapping("/{token}/coupon")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable String token, @RequestBody Coupon coupon) {
        CompanyServiceImpl service = getService(token);
        return ResponseEntity.ok(service.updateCoupon(coupon));
    }

    @PutMapping("/{token}/{couponId}/coupon")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable String token, @PathVariable long couponId,@RequestBody Coupon coupon) {
        CompanyServiceImpl service = getService(token);
        coupon.setId(couponId);
        return ResponseEntity.ok(service.updateCoupon(coupon));
    }

    @DeleteMapping("/{token}/{couponId}/coupon")
    public ResponseEntity<Coupon> deleteCouponById(@PathVariable String token, @PathVariable long couponId) {
        CompanyServiceImpl service = getService(token);
        service.deleteCoupon(couponId);

        return ResponseEntity.ok(Coupon.empty());
    }

    @GetMapping("/{token}")
    public ResponseEntity<Company> getCompanyByToken(@PathVariable String token) {
        CompanyServiceImpl service = getService(token);
        Company company = service.findById();
        return ResponseEntity.ok(company);
    }

    @DeleteMapping("/{token}")
    public ResponseEntity<Company> deleteCompanyByToken(@PathVariable String token) {
        CompanyServiceImpl service = getService(token);
        service.deleteById();
        synchronized (tokensMap) {
            tokensMap.remove(token);
        }

        return ResponseEntity.ok(Company.empty());
    }

    @PutMapping("/{token}")
    public ResponseEntity<Company> updateCompany(@PathVariable String token, @RequestBody Company company) {
        CompanyServiceImpl service = getService(token);
        return ResponseEntity.ok(service.update(company));
    }

    @GetMapping("/{token}/coupons")
    ResponseEntity<List<Coupon>> findCompanyCoupons(@PathVariable String token) {
        CompanyServiceImpl service = getService(token);
        return ResponseEntity.ok(service.findCompanyCoupons());
    }

    @GetMapping("/{token}/coupons/{category}")
    ResponseEntity<List<Coupon>> findCompanyCouponsByCategory(@PathVariable String token, @PathVariable int category) {
        CompanyServiceImpl service = getService(token);

        return ResponseEntity.ok(service.findCompanyCouponsByCategory(category));
    }

    @GetMapping("/{token}/coupons/price/{price}")
    ResponseEntity<List<Coupon>> findCompanyCouponsLessThan(@PathVariable String token, @PathVariable double price) {
        CompanyServiceImpl service = getService(token);
        return ResponseEntity.ok(service.findCompanyCouponsLessThan(price));
    }

    @GetMapping("/{token}/coupons/date/{date}")
    ResponseEntity<List<Coupon>> findCompanyCouponsBeforeDate(@PathVariable String token, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        CompanyServiceImpl service = getService(token);
        return ResponseEntity.ok(service.findCompanyCouponsBeforeDate(date));
    }

    @GetMapping("/{token}/emails_my_customers")
    ResponseEntity<List<String>> findEmailsMyCustomers(@PathVariable String token){
        CompanyServiceImpl service = getService(token);
        return ResponseEntity.ok(service.findEmailsMyCustomers());
    }

    private CompanyServiceImpl getService(String token) {
        ClientSession clientSession = tokensMap.get(token);
        if (null == clientSession) {
            throw new InvalidLoginException("You aren't authorized");
        }else {
            clientSession.accessed();
        }

        AbsService absService = clientSession.getService();

        if (!(absService instanceof CompanyServiceImpl)) {
            throw new InvalidLoginException("You aren't authorized");
        }
        CompanyServiceImpl service = (CompanyServiceImpl)absService;
        return service;
    }
}
