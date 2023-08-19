package com.nwachukwufavour.dronetransport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DroneTransportApplication {

	public static void main(String[] args) {
		SpringApplication.run(DroneTransportApplication.class, args);
	}

}
