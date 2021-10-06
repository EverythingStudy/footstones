package com.whw.footstones;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.whw.footstones.dao")
public class BasicApplication {
    

    public static void main(String[] args) {
        SpringApplication.run(BasicApplication.class, args);
    }

}
