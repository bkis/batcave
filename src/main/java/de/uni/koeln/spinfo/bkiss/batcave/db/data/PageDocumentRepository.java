package de.uni.koeln.spinfo.bkiss.batcave.db.data;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository interface for managing PageDocument objects in DB
 * @author kiss
 *
 */
public interface PageDocumentRepository extends MongoRepository<PageDocument, String> {

    public PageDocument findById(String id);
    public PageDocument findByVolume(String volume);
    public List<PageDocument> findByLanguages(String[] languages);

}
