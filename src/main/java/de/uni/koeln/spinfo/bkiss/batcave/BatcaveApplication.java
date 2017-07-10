package de.uni.koeln.spinfo.bkiss.batcave;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Only used for test runs in IDE
 */
@SpringBootApplication
public class BatcaveApplication implements CommandLineRunner {
	
	public static void main(String[] args) {
		SpringApplication.run(BatcaveApplication.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		//...
	}
}