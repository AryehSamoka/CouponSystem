package com.aryeh.CouponSystem.data.repository;

import com.aryeh.CouponSystem.data.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByEmailAndPassword(String email, String password);
}
