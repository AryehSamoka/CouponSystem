package com.aryeh.CouponSystem.data.repository;

import com.aryeh.CouponSystem.data.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
}
