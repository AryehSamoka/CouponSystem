package com.aryeh.CouponSystem.Service;

import com.aryeh.CouponSystem.data.entity.Client;
import com.aryeh.CouponSystem.data.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientServiceImpl implements clientService {
    private ClientRepository clientRepository;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }


    @Override
    public Optional<Client> getClientByEmailAndPassword(String email, String password) {
        return clientRepository.findByEmailAndPassword(email,password);
    }
}
