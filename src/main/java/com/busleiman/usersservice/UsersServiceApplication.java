package com.busleiman.usersservice;

import com.busleiman.usersservice.persistance.UserRepository;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class UsersServiceApplication{

	public static void main(String[] args) {

		SpringApplication.run(UsersServiceApplication.class, args);


	}
}
