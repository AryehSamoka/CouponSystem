package com.aryeh.CouponSystem.Service;

import com.aryeh.CouponSystem.data.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserByEmailAndPassword(String email, String password);
}
