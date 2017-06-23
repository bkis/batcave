package de.uni.koeln.spinfo.bkiss.batcave.analysis;

import java.io.File;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AnalysisService {
	
	public static final String VECTOR_TYPE_TAG_NEIGHBORS = "tag-neighbors";
	public static final String VECTOR_TYPE_CO_OCCURRENCES = "co-occ";
	
	@Value("${data.vectors}")
	private String vectorsDirPath;
	
	private File vectorsDir;
	private Map<String, AbstractVectorSpace> vectorSpaces;
	
	
	public AnalysisService(){
		
	}
	
	
	public String createVectorSpace(String type){
		init();
		
		if (type.equalsIgnoreCase(VECTOR_TYPE_TAG_NEIGHBORS)){
			
		} else if (type.equalsIgnoreCase(VECTOR_TYPE_CO_OCCURRENCES)){
			return "Not available";
		}
		return "";
	}
	
	
	public String loadVectorSpace(String type){
		init();
		
		if (type.equalsIgnoreCase(VECTOR_TYPE_TAG_NEIGHBORS)){
			
		} else if (type.equalsIgnoreCase(VECTOR_TYPE_CO_OCCURRENCES)){
			return "Not available";
		}
		return "";
	}
	
	
	private void init(){
		if (vectorsDir == null){
			vectorsDir = new File(vectorsDirPath);
		}
		if (!vectorsDir.exists()){
			vectorsDir.mkdirs();
		}
	}

}
