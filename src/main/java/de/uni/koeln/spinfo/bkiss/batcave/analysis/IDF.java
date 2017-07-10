package de.uni.koeln.spinfo.bkiss.batcave.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni.koeln.spinfo.bkiss.batcave.db.data.PageDocument;
import de.uni.koeln.spinfo.bkiss.batcave.db.data.Token;

/**
 * Helper class for IDF values
 * @author kiss
 *
 */
public class IDF {
	
	/**
	 * calculates the IDF values of every token in the given documents
	 * @param pages
	 * @return
	 */
	public static Map<String, Double> idf(List<PageDocument> pages){
		//prepare output structure
		Map<String, Double> idf = new HashMap<String, Double>();
		
		//get raw texts
		System.out.println("[IDF] Reading texts ...");
		List<String> docs = new ArrayList<String>();
		for (PageDocument page : pages) docs.add(getText(page));
		
		//get types and doc count
		System.out.println("[IDF] Collecting types and occurrences ...");
		Map<String, Integer> types = getTypes(docs);
		
		//calculate
		System.out.println("[IDF] Calculating IDF values ...");
		for (String type : types.keySet()){
			idf.put(type, Math.log((double)docs.size() / (double)types.get(type)));
		}
		
		System.out.println("[IDF] Done.");
		return idf;
	}
	
	/*
	 * counts the documents each token appears in
	 */
	private static Map<String, Integer> getTypes(List<String> docs){
		Map<String, Integer> types = new HashMap<String,Integer>();
		Set<String> docTypes = new HashSet<String>();
		int count = 0;
		
		for (String doc : docs){
			count++;
			if (count % 100 == 0)
				System.out.println("[IDF] Processing documents: " + count + " / " + docs.size());
			
			for (String token : doc.split("\\s+")){
				docTypes.add(token);
				if (!types.containsKey(token)){
					types.put(token, 0);
				}
			}
			for (String docType : docTypes){
				types.put(docType, types.get(docType) + 1);
			}
			docTypes.clear();
		}
		
		return types;
	}
	
	/*
	 * concatenates the tokens of a PageDocument to plain text
	 */
	private static String getText(PageDocument page){
		StringBuilder sb = new StringBuilder();
		for (Token t : page.getTokens()){
			sb.append(t.getForm().toUpperCase().replaceAll("\\P{L}+", ""));
			sb.append(" ");
		}
		return sb.toString();
	}
	

}
