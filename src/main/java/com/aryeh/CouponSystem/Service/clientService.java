package com.aryeh.CouponSystem.Service;

import com.aryeh.CouponSystem.data.entity.Client;

import java.util.Optional;


public interface clientService {
    Optional<Client> getClientByEmailAndPassword(String email, String password);
}
