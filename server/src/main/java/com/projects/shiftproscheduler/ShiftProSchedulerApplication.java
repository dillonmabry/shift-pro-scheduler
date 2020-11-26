package com.projects.shiftproscheduler;

import com.projects.shiftproscheduler.optimizer.Optimizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ShiftProSchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShiftProSchedulerApplication.class, args);
        //Optimizer op = new Optimizer();
        //op.testOptimize();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
