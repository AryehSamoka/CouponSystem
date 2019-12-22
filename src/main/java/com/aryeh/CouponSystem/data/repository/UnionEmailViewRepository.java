package com.aryeh.CouponSystem.data.repository;

import com.aryeh.CouponSystem.data.entity.UnionEmailView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnionEmailViewRepository extends JpaRepository<UnionEmailView, Long> {
    @Query("select u.email from UnionEmailView u")
    List<String> findAllEmails();
}
