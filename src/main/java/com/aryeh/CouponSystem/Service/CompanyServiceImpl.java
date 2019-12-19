package com.aryeh.CouponSystem.Service;

import com.aryeh.CouponSystem.data.entity.Company;
import com.aryeh.CouponSystem.data.entity.Coupon;
import com.aryeh.CouponSystem.data.repository.CompanyRepository;
import com.aryeh.CouponSystem.data.repository.CouponRepository;
import com.aryeh.CouponSystem.rest.ex.InvalidCouponAccessException;
import com.aryeh.CouponSystem.rest.ex.NoSuchCouponException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CompanyServiceImpl extends AbsService implements CompanyService {
    private long clientId;

    private CouponRepository couponRepository;
    private CompanyRepository companyRepository;

    @Autowired
    public CompanyServiceImpl(CouponRepository couponRepository, CompanyRepository companyRepository) {
        this.couponRepository = couponRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    @Transactional
    public Company findById() {
        return companyRepository.findById(clientId)
                .orElse(Company.empty());
    }

    @Override
    @Transactional
    public void deleteById() {
        companyRepository.deleteById(clientId);
    }

    @Override
    @Transactional
    public Company update(Company company) {
        if (company.getId() == clientId || company.getId() == 0) {
            company.setId(clientId);
            company.checkPassword(findById());
            removeCouponsOfOtherCompanies(company);

            return companyRepository.save(company);
        }
        return Company.empty();
    }

    @Override
    @Transactional
    public Coupon addCoupon(Coupon coupon) {
        coupon.setId(0);
        coupon.setCompany(findById());
        return couponRepository.save(coupon);
    }

    @Override
    @Transactional
    public Company addCoupons(List<Coupon> coupons) {
        Iterator<Coupon> it = coupons.iterator();
        while (it.hasNext()) {
            addCoupon(it.next());
        }
        return findById();
    }

    @Override
    @Transactional
    public Coupon updateCoupon(Coupon coupon) {
        Company company = checkCouponExistenceInDB(coupon.getId()).getCompany();
        checkCompanyOfCoupon(company);
        coupon.setCompany(company);
        return couponRepository.save(coupon);
    }

    @Override
    @Transactional
    public void deleteCoupon(long couponId) {
        Company company = checkCouponExistenceInDB(couponId).getCompany();
        checkCompanyOfCoupon(company);
        couponRepository.deleteById(couponId);
    }

    @Override
    @Transactional
    public List<Coupon> findCompanyCoupons() {
        return couponRepository.findByCompanyId(clientId);
    }

    @Override
    @Transactional
    public List<Coupon> findCompanyCouponsByCategory(int category) {
        return couponRepository.findByCompanyIdAndCategory(clientId, category);
    }

    @Override
    @Transactional
    public List<Coupon> findCompanyCouponsLessThan(double price) {
        return couponRepository.findByCompanyIdAndPriceLessThan(clientId, price);
    }

    @Override
    @Transactional
    public List<Coupon> findCompanyCouponsBeforeDate(LocalDate date) {
        return couponRepository.findByCompanyIdAndEndDateBefore(clientId, date);
    }

    @Override
    @Transactional
    public List<String> findEmailsMyCustomers() {
        return companyRepository.findEmailsMyCustomers(clientId);
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    private void removeCouponsOfOtherCompanies(Company company) {
        Set<Coupon> coupons = company.getCoupons();
        Iterator<Coupon> couponsIterator = coupons.iterator();
        while (couponsIterator.hasNext()) {
            removeCouponOfOtherCompanies(couponsIterator);
        }
    }

    /**
     * Removing coupons of other companies or with non existing id's.
     * I didn't want to throw an exception and terminate the update for some coupons
     * who aren't present or aren't from this company.
     * However the wrong coupons won't be updated at all and won't be seen in the response body
     *
     * @param couponsIterator
     */
    private void removeCouponOfOtherCompanies(Iterator<Coupon> couponsIterator) {
        Optional<Coupon> optCoupon = couponRepository.findById(couponsIterator.next().getId());
        if (optCoupon.isPresent()) {
            Coupon coupon = optCoupon.get();
            if (coupon.getCompany().getId() != clientId) {
                couponsIterator.remove();
            }
        } else {
            couponsIterator.remove();
        }
    }

    private Coupon checkCouponExistenceInDB(long couponId) {
        Optional<Coupon> optionalCoupon = couponRepository.findById(couponId);
        if (!optionalCoupon.isPresent()) {
            throw new NoSuchCouponException("");
        }
        return optionalCoupon.get();
    }

    private void checkCompanyOfCoupon(Company company) {
        if (!(company != null && company.getId() == clientId)) {
            throw new InvalidCouponAccessException("");
        }
    }
}
