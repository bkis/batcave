package de.uni.koeln.spinfo.bkiss.batcave;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.uni.koeln.spinfo.bkiss.batcave.db.data.PageDocument;
import de.uni.koeln.spinfo.bkiss.batcave.db.data.PageDocumentRepository;

@SpringBootApplication
public class BatcaveApplication implements CommandLineRunner {
	
	@Autowired
	private PageDocumentRepository repo;

	public static void main(String[] args) {
		SpringApplication.run(BatcaveApplication.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		repo.deleteAll();
		repo.save(new PageDocument("s76s76s75s"));
		repo.save(new PageDocument("ijuoi34iu43oiu34"));
	}
}