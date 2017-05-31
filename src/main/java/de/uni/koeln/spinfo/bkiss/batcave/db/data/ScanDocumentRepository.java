package de.uni.koeln.spinfo.bkiss.batcave.db.data;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface ScanDocumentRepository extends MongoRepository<ScanDocument, String> {

    public ScanDocument findById(String id);
    public ScanDocument findByImageFile(String imageFile);

}
