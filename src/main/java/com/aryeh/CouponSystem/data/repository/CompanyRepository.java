package com.aryeh.CouponSystem.data.repository;

import com.aryeh.CouponSystem.data.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByEmailAndPassword(String email, String password);


    @Query("from Company t1 left join t1.coupons t2 where t2.id is null")
    List<Company> findAllCompaniesWithoutCoupons();

    @Query("select co.email from Company co")
    List<String> findAllEmails();
}
