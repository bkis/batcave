package de.uni.koeln.spinfo.bkiss.batcave.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.uni.koeln.spinfo.bkiss.batcave.db.data.PageDocument;
import de.uni.koeln.spinfo.bkiss.batcave.db.data.PageDocumentRepository;
import de.uni.koeln.spinfo.bkiss.batcave.db.data.Similarity;
import de.uni.koeln.spinfo.bkiss.batcave.db.data.SimilarityRepository;
import de.uni.koeln.spinfo.bkiss.batcave.db.data.Token;
import de.uni.koeln.spinfo.bkiss.batcave.utils.CollectionTools;

@Service
public class AnalysisService {
	
	@Autowired
	private SimilarityRepository simRepo;
	
	@Autowired
	private PageDocumentRepository pageRepo;
	
	
	public AnalysisService(){
		
	}
	
	
	public Map<String, Double> sims(String to, String lang, int n){
		Similarity result = simRepo.findByTokenAndLanguage(to.toUpperCase(), lang);
		return result != null ? result.getMostSimilar(n) : new HashMap<String, Double>();
	}
	
	
	public String createSimilarityData(){
		Set<String> languages = new HashSet<String>();
		for (PageDocument page : pageRepo.findAll()){
			languages.addAll(page.getLanguages());
		}
		
		String[] l = languages.toArray(new String[]{});
		return createSimilarityData(l);
	}
	
	
	public String createSimilarityData(String[] languages){
		StringBuilder sb = new StringBuilder();
		for (String language : languages){
			sb.append(createSimilarityData(language));
			sb.append("\n");
		}
		return sb.toString();
	}
	
	
	public String createSimilarityData(String language){
		System.gc();
		
		//remove old semantics data
		simRepo.delete(simRepo.findByLanguage(language));
		
		//get raw data
		List<PageDocument> data = pageRepo.findByLanguages(new String[]{language});
		
		//get idf for types
		Map<String, Double> idf = IDF.idf(data);
		//TODO use idf
		
		Map<String, Integer> dimensions = new HashMap<String, Integer>();
		Map<String, Double[]> countMap = new HashMap<String, Double[]>();
		
		//create dimensions map
		System.out.println("[SemanticVectorSpace] creating dimensions map...");
		for (PageDocument page : data){
			//collect and count types
			for (Token token : page.getTokens()){
				String type = cleanToken(token.getForm());
				if (type.length() < 2) continue;
				if (!dimensions.containsKey(type))
					dimensions.put(type, 1);
				else
					dimensions.put(type, dimensions.get(type) + 1);
			}
		}
		//sort by count
		dimensions = CollectionTools.sortMapByValue(dimensions, false);
		//keep most frequent
		dimensions = CollectionTools.trimMap(dimensions, Math.min(dimensions.size(), 1000)); //TODO mehr!
		dimensions = fillIndexMap(dimensions);
		System.out.println("[SemanticVectorSpace] Done. Dimensions: " + dimensions.size());

		//create absolute vectors
		System.out.println("[SemanticVectorSpace] creating absolute count vectors...");
		Double[] dims = new Double[dimensions.size()];
		Arrays.fill(dims, 0.0);
		
		int count = 0;
		for (PageDocument page : data){
			//construct page text
			StringBuilder sb = new StringBuilder();
			for (Token t : page.getTokens()){
				String token = cleanToken(t.getForm());
				if (token.length() < 2) continue;
				sb.append(token.toUpperCase());
				sb.append(" ");
			}
			
			Set<String> tokens = new HashSet<String>(Arrays.asList(sb.toString().split(" ")));
			
			for (String token : tokens){
				
				if (!countMap.containsKey(token))
					countMap.put(token, Arrays.copyOf(dims, dims.length));
				
				List<String[]> contexts = trimTextMulti(sb.toString(), token, 5, false); //get context coOccs
				//add co-occs to matrix map
				for (String[] context : contexts){
					for (String coOcc : context){
						if (dimensions.containsKey(coOcc) && !coOcc.equals(token)){
							countMap.get(token)[dimensions.get(coOcc)]++;
						}
					}
				}
			}
			
			count++;
			if (count % 100 == 0) System.out.println(count + " / " + data.size() + " (" + countMap.size() + " vectors)");
			
		}
		System.out.println("[SemanticVectorSpace] Done. Vectors: " + countMap.size());
		
		//calculate similarities
		System.out.println("[SemanticVectorSpace] Calculating similarities...");
		
		count = 0;
		Map<String, Double> sim;
		Similarity similarity;
		for (String key1 : countMap.keySet()){
			similarity = new Similarity(key1, language);
			sim = new HashMap<String, Double>();
			for (String key2 : countMap.keySet()){
				if (key1.equals(key2)) continue;
				Double value = VectorMath.cosineSimilarity(countMap.get(key1), countMap.get(key2));
				if (value > 0.0) sim.put(key2, value);
			}
			sim = CollectionTools.sortMapByValue(sim, false);
			sim = CollectionTools.trimMap(sim, 30);
			similarity.addMostSimilar(sim);
			simRepo.insert(similarity);
			count++;
			if (count % 100 == 0) System.out.println("[Similarities] " + count + " / " + countMap.size());
		}
		countMap = null;
		System.gc();
		
		return "Vector space for language \"" + language + "\" created.";
	}
	
	
	private List<String[]> trimTextMulti(String text, String around, int contextNrOfWords, boolean useSubstrings) {
		List<String[]> out = new ArrayList<String[]>();
		String[] tokens = text.replaceAll("\\P{L}", " ").replaceAll("\\s+", " ").split(" ");

		around = around.toUpperCase();
		int min;
		int max;
		int ind = -1;

		for (int i = ind + 1; i < tokens.length; i++) {
			if (useSubstrings && !tokens[i].toUpperCase().contains(around))
				continue;
			if (!useSubstrings && !tokens[i].equalsIgnoreCase(around))
				continue;
			ind = i;
			min = Math.max(ind - contextNrOfWords, 0);
			max = Math.min(ind + contextNrOfWords + 1, tokens.length);
			out.add(Arrays.copyOfRange(tokens, min, max));
		}

		return out;
	}
	
	
	private Map<String, Integer> fillIndexMap(Map<String, Integer> map){
		int count = 0;
		for (Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, Integer> e = it.next();
			map.put(e.getKey(), count);
			count++;
		}
		return map;
	}
	
	
	public static String cleanToken(String token){
		return token.replaceAll("\\P{L}+", "").toUpperCase();
	}
	
}
