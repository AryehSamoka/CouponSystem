package com.aryeh.CouponSystem.data.repository;

import com.aryeh.CouponSystem.data.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    @Query("from Company t1 left join t1.coupons t2 where t2.id is null")
    List<Company> findAllCompaniesWithoutCoupons();

    @Query("select distinct t1.email from Customer as t1 join t1.coupons as t2 join t2.company as t3 where t3.id=:companyId")
    List<String> findEmailsMyCustomers(long companyId);

    @Query("select distinct t3.email, t6.email from Customer as t1 join t1.coupons as t2 join t2.company as t3, " +
            "Customer as t4 join t4.coupons as t5 join t5.company as t6 where t3.id <> t6.id and t1.id = t4.id")
    Set<Set<String>> pairsCompaniesSameCustomer();
}
