package com.aryeh.CouponSystem.Service;

import com.aryeh.CouponSystem.data.entity.Client;
import com.aryeh.CouponSystem.data.repository.clientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class clientServiceImpl implements clientService {
    private clientRepository clientRepository;

    @Autowired
    public clientServiceImpl(clientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }


    @Override
    public Optional<Client> getClientByEmailAndPassword(String email, String password) {
        return clientRepository.findByEmailAndPassword(email,password);
    }
}
