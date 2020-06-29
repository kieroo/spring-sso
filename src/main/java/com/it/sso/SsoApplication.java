package com.it.sso;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.it.sso"})
@MapperScan("com.it.sso.dao")
public class SsoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SsoApplication.class);
    }
}
