package com.aryeh.CouponSystem.rest.controller;

import com.aryeh.CouponSystem.Service.CompanyService;
import com.aryeh.CouponSystem.rest.AbsSession;
import com.aryeh.CouponSystem.rest.ClientSession;
import com.aryeh.CouponSystem.rest.CsSystem;
import com.aryeh.CouponSystem.rest.ex.InvalidLoginException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class LoginController {
    public static final int LENGTH_TOKEN = 15;
    private Map<String, ClientSession> tokensMap;
    private CsSystem csSystem;

    @Autowired
    public LoginController(@Qualifier("tokens") Map<String, ClientSession> tokensMap, CsSystem csSystem) {
        this.tokensMap = tokensMap;
        this.csSystem = csSystem;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String userName, @RequestParam String password) {
        try {
            ClientSession clientSession = csSystem.login(userName, password);
            String token = generateToken();

            tokensMap.put(token, clientSession);

            return ResponseEntity.ok(token);

        } catch (InvalidLoginException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
    }

    private static String generateToken() {
        return UUID.randomUUID()
                .toString()
                .replaceAll("-", "")
                .substring(0, LENGTH_TOKEN);
    }
}
