package de.uni.koeln.spinfo.bkiss.batcave.db.data;

import org.springframework.data.mongodb.repository.MongoRepository;


/**
 * Repository interface for managing ScanDocument objects in DB
 * @author kiss
 *
 */
public interface ScanDocumentRepository extends MongoRepository<ScanDocument, String> {

    public ScanDocument findById(String id);
    public ScanDocument findByImageFile(String imageFile);

}
