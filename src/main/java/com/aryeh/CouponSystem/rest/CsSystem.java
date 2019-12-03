package com.aryeh.CouponSystem.rest;

import com.aryeh.CouponSystem.Service.CustomerService;
import com.aryeh.CouponSystem.Service.clientService;
import com.aryeh.CouponSystem.data.entity.Client;
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
    private clientService clientService;
    private Environment env;
    private Map<String, ClientSession> tokensMap;
    private CouponRepository couponRepository;
    private CustomerService customerService;

    @Autowired
    public CsSystem(ApplicationContext context, clientService clientService, Environment env, @Qualifier("tokens") Map<String,
            ClientSession> tokensMap, CouponRepository couponRepository, CustomerService customerService) {
        this.context = context;
        this.clientService = clientService;
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

    public String[] login(String userName, String password) throws InvalidLoginException {

        Client client = getClient(userName, password);

        String[] optToken = checkTokenExistence(client.getId());
        if (optToken != null) {
            return optToken;
        }

        ClientSession clientSession = setupClientSession(client);

        return clientSessionWithToken(clientSession);
    }

    private Client getClient(String userName, String password) {
        Optional<Client> optClient = clientService.getClientByEmailAndPassword(userName, password);

        if (!optClient.isPresent()) {
            throw new InvalidLoginException(String.format("Invalid login with email: %s and password: %s", userName, password));
        }
        return optClient.get();
    }

    private synchronized String[] checkTokenExistence(Long clientId) {
        Iterator<Map.Entry<String, ClientSession>> itr = tokensMap.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, ClientSession> entry = itr.next();
            ClientSession session = entry.getValue();
            if (session.getClientId() == clientId) {
                session.accessed();
                String[] login={entry.getKey(), session.getClientType().toString()};
                return login;
            }
        }
        return null;
    }

    private ClientSession setupClientSession(Client client) {
        ClientSession clientSession = context.getBean(ClientSession.class);

        client.setClientSession(context, clientSession);
        clientSession.accessed();
        return clientSession;
    }

    private String[] clientSessionWithToken(ClientSession clientSession) {
        String token = generateToken();
        synchronized (tokensMap) {
            tokensMap.put(token, clientSession);
        }
        String[] login={token, clientSession.getClientType().toString()};
        return login;
    }

    private static String generateToken() {
        return UUID.randomUUID()
                .toString()
                .replaceAll("-", "")
                .substring(0, LENGTH_TOKEN);
    }
}
