package com.aryeh.CouponSystem.rest;

import com.aryeh.CouponSystem.Service.*;
import com.aryeh.CouponSystem.data.entity.*;
import com.aryeh.CouponSystem.data.repository.CompanyRepository;
import com.aryeh.CouponSystem.data.repository.CustomerRepository;
import com.aryeh.CouponSystem.rest.ex.InvalidLoginException;
import com.aryeh.CouponSystem.threads.ClientSessionCleanerTask;
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

    @Autowired
    public CsSystem(ApplicationContext context, UserService userService, Environment env, @Qualifier("tokens") Map<String, ClientSession> tokensMap) {
        this.context = context;
        this.userService = userService;
        this.env = env;
        this.tokensMap = tokensMap;
    }

    @PostConstruct
    public void init() {
        Thread thread = new Thread(new ClientSessionCleanerTask(tokensMap));
        thread.start();
    }

    public ClientSession login(String userName, String password) throws InvalidLoginException {
        Optional<User> optUser = userService.getUserByEmailAndPassword(userName, password);

        if (!optUser.isPresent()) {
            throw new InvalidLoginException(String.format("Invalid login with email: %s and password: %s", userName, password));
        }
        Client client = optUser.get().getClient();

        ClientSession clientSession = context.getBean(ClientSession.class);

        if (client instanceof Company) {
            Company company = (Company) client;

            CompanyServiceImpl service = context.getBean(CompanyServiceImpl.class);
            service.setCompanyId(company.getId());

            clientSession.setService(service);
        } else if (client instanceof Customer) {
            Customer customer = (Customer) client;

            CustomerServiceImpl service = context.getBean(CustomerServiceImpl.class);
            service.setCustomerId(customer.getId());

            clientSession.setService(service);
        } else {
            AdminServiceImpl service = context.getBean(AdminServiceImpl.class);

            clientSession.setService(service);
        }

        clientSession.accessed();
        return clientSession;
    }
}
