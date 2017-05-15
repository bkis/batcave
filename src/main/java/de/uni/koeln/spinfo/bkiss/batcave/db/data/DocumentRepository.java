package de.uni.koeln.spinfo.bkiss.batcave.db.data;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocumentRepository extends MongoRepository<Document, String> {
	public Document findByTitle(String title);
}
