package com.omi.SeXurityPOC;

import com.omi.SeXurityPOC.config.RSAKeyRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RSAKeyRecord.class)
@SpringBootApplication
public class SeXurityPocApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeXurityPocApplication.class, args);
	}

}
