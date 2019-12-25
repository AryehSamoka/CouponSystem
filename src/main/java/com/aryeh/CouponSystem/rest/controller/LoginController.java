package com.aryeh.CouponSystem.rest.controller;

import com.aryeh.CouponSystem.rest.ClientSession;
import com.aryeh.CouponSystem.rest.CsSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {
    private Map<String, ClientSession> tokensMap;
    private CsSystem csSystem;

    @Autowired
    public LoginController(@Qualifier("tokens") Map<String, ClientSession> tokensMap, CsSystem csSystem) {
        this.tokensMap = tokensMap;
        this.csSystem = csSystem;
    }

    @PostMapping("/login")
    public ResponseEntity<String[]> login(@RequestParam String userName, @RequestParam String password) {
        return ResponseEntity.ok(csSystem.login(userName, password));
    }

    @DeleteMapping("/logout/{token}")
    public ResponseEntity logout(@PathVariable String token){
        synchronized (tokensMap) {
            tokensMap.remove(token);
        }
        return ResponseEntity.accepted().body("You're logged out");
    }
}
