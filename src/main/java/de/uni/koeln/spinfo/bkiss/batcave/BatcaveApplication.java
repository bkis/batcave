package de.uni.koeln.spinfo.bkiss.batcave;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.uni.koeln.spinfo.bkiss.batcave.data.Document;
import de.uni.koeln.spinfo.bkiss.batcave.data.DocumentRepository;

@SpringBootApplication
public class BatcaveApplication implements CommandLineRunner {
	
	@Autowired
	private DocumentRepository repo;

	public static void main(String[] args) {
		SpringApplication.run(BatcaveApplication.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		repo.deleteAll();
		repo.save(new Document("Test", "Dies ist ein Test!"));
		repo.save(new Document("Telefon", "Ich geh ans Telefon ran!"));
	}
}