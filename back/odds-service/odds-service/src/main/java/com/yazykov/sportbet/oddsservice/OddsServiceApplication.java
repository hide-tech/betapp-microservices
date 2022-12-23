package com.yazykov.sportbet.oddsservice;

import com.yazykov.sportbet.oddsservice.config.AWSParameters;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AWSParameters.class)
public class OddsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OddsServiceApplication.class, args);
	}

}
