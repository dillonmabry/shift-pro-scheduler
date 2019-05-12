package com.projects.shiftproscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@SpringBootApplication
public class ShiftProSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShiftProSchedulerApplication.class, args);
	}

}
