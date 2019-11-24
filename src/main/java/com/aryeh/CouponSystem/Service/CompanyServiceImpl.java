package com.aryeh.CouponSystem.Service;

import com.aryeh.CouponSystem.data.entity.Company;
import com.aryeh.CouponSystem.data.entity.Coupon;
import com.aryeh.CouponSystem.data.entity.User;
import com.aryeh.CouponSystem.data.repository.CompanyRepository;
import com.aryeh.CouponSystem.data.repository.CouponRepository;
import com.aryeh.CouponSystem.data.repository.UserRepository;
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
        deleteCompanyUser();
        companyRepository.deleteById(companyId);
    }

    @Override
    @Transactional
    public Company update(Company company) {
        if (company.getId() == companyId || company.getId() == 0) {
            company.setId(companyId);
            Company oldCompany = findById();
            checkPassword(company, oldCompany);
            updateCompanyUser(company, oldCompany);
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
        Company company = checkCouponExistenceInDB(coupon).getCompany();
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

    private void updateCompanyUser(Company company, Company oldCompany) {
        Optional<User> optUser = userRepository.findByEmailAndPassword(oldCompany.getEmail(), oldCompany.returnPassword());
        /*The user has to be present because we got here with token*/
        User user = optUser.get();
        user.setEmail(company.getEmail());
        user.setPassword(company.getPassword());
        userRepository.save(user);
    }

    private void removeCouponsOfOtherCompanies(Company company) {
        List<Coupon> coupons = company.getCoupons();
        Iterator<Coupon> couponsIterator = coupons.iterator();
        while (couponsIterator.hasNext()) {
            removeCouponOfOtherCompanies(couponsIterator);
        }
    }

    /**
     * Removing coupons of other companies or with non existing id's
     * <p>
     * I didn't want to throw an exception and terminate the update for some coupons
     * who aren't present or aren't from this company.
     * However the wrong coupons won't be updated at all and can't be seen in the response body
     *
     * @param couponsIterator
     */
    private void removeCouponOfOtherCompanies(Iterator<Coupon> couponsIterator) {
        Optional<Coupon> optCoupon = couponRepository.findById(couponsIterator.next().getId());
        if (optCoupon.isPresent()) {
            Coupon coupon = optCoupon.get();
            if (coupon.getCompany().getId() != companyId) {
                couponsIterator.remove();
            }
        } else {
            couponsIterator.remove();
        }
    }

    private void deleteCompanyUser() {
        Company company = findById();
        Optional<User> optUser = userRepository.findByEmailAndPassword(company.getEmail(), company.returnPassword());
        if (optUser.isPresent()) {
            userRepository.deleteById(optUser.get().getId());
        }
    }

    private Coupon checkCouponExistenceInDB(Coupon coupon) {
        Optional<Coupon> optionalCoupon = couponRepository.findById(coupon.getId());
        if (!optionalCoupon.isPresent()) {
            throw new NoSuchCouponException("");
        }
        return optionalCoupon.get();
    }

    private Coupon checkCouponExistenceInDB(long couponId) {
        Optional<Coupon> optionalCoupon = couponRepository.findById(couponId);
        if (!optionalCoupon.isPresent()) {
            throw new NoSuchCouponException("");
        }
        return optionalCoupon.get();
    }

    private void checkCompanyOfCoupon(Company company) {
        if (!(company != null && company.getId() == companyId)) {
            throw new InvalidCouponAccessException("");
        }
    }

    /**
     * The password will be checked and if it is null the old password will stay,
     * the reason is that only the password can't be seen by findById because of json ignore.
     * On client to check what will be counted as null.
     * @param company
     * @param oldCompany
     */
    private void checkPassword(Company company, Company oldCompany) {
        if(company.getPassword() == null){
            company.setPassword(oldCompany.getPassword());
            System.out.println(company.getPassword());
        }
    }
}
