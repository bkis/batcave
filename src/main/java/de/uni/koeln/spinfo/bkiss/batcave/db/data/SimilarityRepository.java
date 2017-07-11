package de.uni.koeln.spinfo.bkiss.batcave.db.data;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository interface for managing Similarity objects in DB
 * @author kiss
 *
 */
public interface SimilarityRepository extends MongoRepository<Similarity, String> {

    public List<Similarity> findByLanguage(String language);
    public List<Similarity> findByToken(String token);
    public Similarity findByTokenAndLanguage(String token, String language);

}
