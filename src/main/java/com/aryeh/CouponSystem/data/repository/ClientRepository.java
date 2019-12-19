package com.aryeh.CouponSystem.data.repository;

import com.aryeh.CouponSystem.data.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmailAndPassword(String email, String password);
}
