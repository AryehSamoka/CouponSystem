package com.aryeh.CouponSystem.data.repository;

import com.aryeh.CouponSystem.data.entity.UnionClientView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnionClientViewRepository extends JpaRepository<UnionClientView, Long> {
    @Query("select u.email from UnionClientView u")
    List<String> findAllEmails();

    @Query("select u.password from UnionClientView u where u.email=:email")
    Optional<String> findPasswordByEmail(String email);
}
