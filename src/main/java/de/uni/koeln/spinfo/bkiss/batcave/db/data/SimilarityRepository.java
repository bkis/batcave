package de.uni.koeln.spinfo.bkiss.batcave.db.data;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface SimilarityRepository extends MongoRepository<Similarity, String> {

    public List<Similarity> findByLanguage(String language);
    public Similarity findByTokenAndLanguage(String token, String language);

}
