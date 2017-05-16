package de.uni.koeln.spinfo.bkiss.batcave.db.data;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PageDocumentRepository extends MongoRepository<PageDocument, String> {
	public PageDocument findById(String id);
}
