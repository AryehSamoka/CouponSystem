package com.aryeh.CouponSystem.rest;

import com.aryeh.CouponSystem.Service.AdminServiceImpl;
import com.aryeh.CouponSystem.Service.CompanyServiceImpl;
import com.aryeh.CouponSystem.Service.CustomerServiceImpl;
import com.aryeh.CouponSystem.Service.UserService;
import com.aryeh.CouponSystem.data.entity.*;
import com.aryeh.CouponSystem.data.repository.CouponRepository;
import com.aryeh.CouponSystem.rest.ex.InvalidLoginException;
import com.aryeh.CouponSystem.threads.ClientSessionCleanerTask;
import com.aryeh.CouponSystem.threads.CouponCleanerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;

@Service
public class CsSystem {

    private ApplicationContext context;
    private UserService userService;
    private Environment env;
    private Map<String, ClientSession> tokensMap;
    private CouponRepository couponRepository;

    @Autowired
    public CsSystem(ApplicationContext context, UserService userService, Environment env, @Qualifier("tokens") Map<String,
            ClientSession> tokensMap, CouponRepository couponRepository) {
        this.context = context;
        this.userService = userService;
        this.env = env;
        this.tokensMap = tokensMap;
        this.couponRepository = couponRepository;
    }

    @PostConstruct
    public void init() {
        new Thread(new ClientSessionCleanerTask(tokensMap)).start();
        new Thread(new CouponCleanerTask(couponRepository)).start();
    }

    public ClientSession login(String userName, String password) throws InvalidLoginException {
        Optional<User> optUser = userService.getUserByEmailAndPassword(userName, password);

        if (!optUser.isPresent()) {
            throw new InvalidLoginException(String.format("Invalid login with email: %s and password: %s", userName, password));
        }
        Client client = optUser.get().getClient();

        ClientSession clientSession = context.getBean(ClientSession.class);

        if (client instanceof Company) {
            CompanyServiceImpl service = context.getBean(CompanyServiceImpl.class);
            service.setCompanyId(client.getId());

            clientSession.setService(service);
        } else if (client instanceof Customer) {
            CustomerServiceImpl service = context.getBean(CustomerServiceImpl.class);
            service.setCustomerId(client.getId());

            clientSession.setService(service);
        } else {
            AdminServiceImpl service = context.getBean(AdminServiceImpl.class);
            service.setAdminId(client.getId());

            clientSession.setService(service);
        }

        clientSession.accessed();
        return clientSession;
    }
}
