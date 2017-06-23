package de.uni.koeln.spinfo.bkiss.batcave.analysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni.koeln.spinfo.bkiss.batcave.db.data.PageDocument;
import de.uni.koeln.spinfo.bkiss.batcave.db.data.Token;

public class TagNeighborsVectorSpace extends AbstractVectorSpace {
	
	private static final long serialVersionUID = 1L;

	public TagNeighborsVectorSpace(String vectorsDirPath){
		super("tag_neighbors", vectorsDirPath);
	}
	

	@Override
	public void createVectorSpace(List<PageDocument> data) {
		Map<String, Integer> dimensions = new HashMap<String, Integer>();
		vectorSpace = new HashMap<String, Map<String, Integer>>();
		
		//create dimensions map
		for (PageDocument page : data){
			for (Token token : page.getTokens()){
				for (String tag : token.getTags()){
					for (String pos : new String[]{"_PREV", "_NEXT"}){
						if (!dimensions.containsKey(tag + pos)){
							dimensions.put(tag + pos, 0);
						}
					}
				}
			}
		}
		
		//create absolute vectors
		for (PageDocument page : data){
			for (int i = 0; i < page.getTokens().size(); i++){
				
				String token = cleanToken(page.getTokens().get(i).getForm());
				if (!vectorSpace.containsKey(token))
					vectorSpace.put(token, new HashMap<String, Integer>(dimensions));
				
				//prev tags
				if (i > 0){
					for (String tag : page.getTokens().get(i-1).getTags()){
						vectorSpace.get(token).put(
								tag + "_PREV", vectorSpace.get(token).get(tag + "_PREV") + 1);
					}
				}
				
				//next tags
				if (i < page.getTokens().size() - 1){
					for (String tag : page.getTokens().get(i+1).getTags()){
						vectorSpace.get(token).put(
								tag + "_NEXT", vectorSpace.get(token).get(tag + "_NEXT") + 1);
					}
				}
			}
		}
		
		//write vector space file
		try {
			writeVectorSpace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private String cleanToken(String token){
		return token.replaceAll("\\P{L}", "");
	}
	
}
