package com.aryeh.CouponSystem.data.repository;

import com.aryeh.CouponSystem.data.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    @Query("from Company t1 left join t1.coupons t2 where t2.id is null")
    List<Company> findAllCompaniesWithoutCoupons();

    @Query("select distinct t1.email from Customer as t1 join t1.coupons as t2 join t2.company as t3 where t3.id=:companyId")
    List<String> findEmailsMyCustomers(long companyId);
}
