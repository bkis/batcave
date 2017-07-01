package de.uni.koeln.spinfo.bkiss.batcave.db.data;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import de.uni.koeln.spinfo.bkiss.batcave.utils.CollectionTools;

@Document(collection="semantics")
public class Similarity {
	
	@Id
	private String id;
	
	private String token;
	private String language;
	private Map<String, Double> mostSimilar;
	
	
	public Similarity(String token, String language) {
		super();
		this.token = token;
		this.language = language;
		this.mostSimilar = new TreeMap<String, Double>();
	}
	
	
	public void setId(String id){
		this.id = id;
	}
	
	public String getId(){
		return id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Map<String, Double> getMostSimilar() {
		return mostSimilar;
	}
	
	public Map<String, Double> getMostSimilar(int nMostSimilar) {
		return CollectionTools.trimMap(mostSimilar, nMostSimilar);
	}

	public void addMostSimilar(String token, Double similarity) {
		mostSimilar.put(token, similarity);
	}
	
	public void addMostSimilar(Map<String, Double> similarities){
		for (Entry<String, Double> e : similarities.entrySet()){
			mostSimilar.put(e.getKey(), e.getValue());
		}
		sort();
	}
	
	public void sort(){
		this.mostSimilar = CollectionTools.sortMapByValue(mostSimilar, false);
	}
	
}
