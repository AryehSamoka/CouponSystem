package com.aryeh.CouponSystem.data.repository;

import com.aryeh.CouponSystem.data.entity.Client;
import com.aryeh.CouponSystem.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndPassword(String email, String password);
}
