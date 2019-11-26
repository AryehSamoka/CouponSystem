package com.aryeh.CouponSystem.rest;

import com.aryeh.CouponSystem.Service.*;
import com.aryeh.CouponSystem.data.entity.Client;
import com.aryeh.CouponSystem.data.entity.Company;
import com.aryeh.CouponSystem.data.entity.Customer;
import com.aryeh.CouponSystem.data.entity.User;
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
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class CsSystem {

    public static final int LENGTH_TOKEN = 15;
    private ApplicationContext context;
    private UserService userService;
    private Environment env;
    private Map<String, ClientSession> tokensMap;
    private CouponRepository couponRepository;
    private CustomerService customerService;

    @Autowired
    public CsSystem(ApplicationContext context, UserService userService, Environment env, @Qualifier("tokens") Map<String,
            ClientSession> tokensMap, CouponRepository couponRepository, CustomerService customerService) {
        this.context = context;
        this.userService = userService;
        this.env = env;
        this.tokensMap = tokensMap;
        this.couponRepository = couponRepository;
        this.customerService = customerService;
    }

    @PostConstruct
    public void init() {
        new Thread(new ClientSessionCleanerTask(tokensMap)).start();
        new Thread(new CouponCleanerTask(customerService, couponRepository)).start();
    }

    public String login(String userName, String password) throws InvalidLoginException {

        User user = getUser(userName, password);
        long userId = user.getId();
        String optToken = checkTokenExistence(userId);
        if(optToken != null){
            return optToken;
        }
        Client client = user.getClient();

        ClientSession clientSession = context.getBean(ClientSession.class);
        clientSession.setUserId(userId);

        client.setClientSession(context, clientSession);

        clientSession.accessed();
        String token = generateToken();
        tokensMap.put(token, clientSession);

        return token;
    }

    private String checkTokenExistence(Long userId) {
        Iterator<Map.Entry<String, ClientSession>> itr = tokensMap.entrySet().iterator();
        while(itr.hasNext())
        {
            Map.Entry<String, ClientSession> entry = itr.next();
            ClientSession session = entry.getValue();
            if(session.getUserId() == userId){
                session.accessed();
                return entry.getKey();
            }
        }
        return null;
    }

    private User getUser(String userName, String password) {
        Optional<User> optUser = userService.getUserByEmailAndPassword(userName, password);

        if (!optUser.isPresent()) {
            throw new InvalidLoginException(String.format("Invalid login with email: %s and password: %s", userName, password));
        }
        return optUser.get();
    }

    private static String generateToken() {
        return UUID.randomUUID()
                .toString()
                .replaceAll("-", "")
                .substring(0, LENGTH_TOKEN);
    }
}
