package com.aryeh.CouponSystem.Service;

import com.aryeh.CouponSystem.data.entity.Company;
import com.aryeh.CouponSystem.data.entity.Coupon;
import com.aryeh.CouponSystem.data.entity.User;
import com.aryeh.CouponSystem.data.repository.CompanyRepository;
import com.aryeh.CouponSystem.data.repository.CouponRepository;
import com.aryeh.CouponSystem.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CompanyServiceImpl extends AbsService implements CompanyService {
    private long companyId;

    private CouponRepository couponRepository;
    private CompanyRepository companyRepository;
    private UserRepository userRepository;

    @Autowired
    public CompanyServiceImpl(CouponRepository couponRepository, CompanyRepository companyRepository, UserRepository userRepository) {
        this.couponRepository = couponRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Company findById() {
        return companyRepository.findById(companyId)
                .orElse(Company.empty());
    }

    @Override
    @Transactional
    public void deleteById() {
        Optional<User> optUser = userRepository.findByClientId(companyId, 1);
        if(optUser.isPresent()) {
            userRepository.deleteById(optUser.get().getId());
        }
        companyRepository.deleteById(companyId);
    }

    @Override
    @Transactional
    public Company update(Company company) {
        if (company.getId() == companyId || company.getId() == 0) {
            company.setId(companyId);
            /*Todo coupons empty list*/
            return companyRepository.save(company);
        }
        return Company.empty();
    }

    @Override
    @Transactional
    public Company login(String email, String password) {
        return companyRepository.findByEmailAndPassword(email, password)
                .orElse(Company.empty());
    }

    @Override
    @Transactional
    public List<Coupon> findCompanyCoupons() {
        return couponRepository.findByCompanyId(companyId);
    }

    @Override
    @Transactional
    public List<Coupon> findCompanyCouponsByCategory(int category) {
        return couponRepository.findByCompanyIdAndCategory(companyId, category);
    }

    @Override
    @Transactional
    public List<Coupon> findCompanyCouponsLessThan(double price) {
        return couponRepository.findByCompanyIdAndPriceLessThan(companyId, price);
    }

    @Override
    @Transactional
    public List<Coupon> findCompanyCouponsBeforeDate(LocalDate date) {
        return couponRepository.findByCompanyIdAndEndDateBefore(companyId, date);
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }
}
