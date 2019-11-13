package com.aryeh.CouponSystem;

import com.aryeh.CouponSystem.threads.ClientSessionCleanerTask;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CouponSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(CouponSystemApplication.class, args);
	}

}
