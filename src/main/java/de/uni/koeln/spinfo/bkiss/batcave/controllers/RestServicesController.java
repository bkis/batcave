package de.uni.koeln.spinfo.bkiss.batcave.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import de.uni.koeln.spinfo.bkiss.batcave.analysis.AnalysisService;
import de.uni.koeln.spinfo.bkiss.batcave.analysis.IDF;
import de.uni.koeln.spinfo.bkiss.batcave.db.data.PageDocument;
import de.uni.koeln.spinfo.bkiss.batcave.db.data.PageDocumentRepository;
import de.uni.koeln.spinfo.bkiss.batcave.db.data.ScanDocumentRepository;
import de.uni.koeln.spinfo.bkiss.batcave.search.SearchService;
import de.uni.koeln.spinfo.bkiss.batcave.utils.CollectionTools;


@RestController
public class RestServicesController {
	
	@Autowired
	ScanDocumentRepository scanRepo;
	
	@Autowired
	PageDocumentRepository pageRepo;
	
	@Autowired
	private SearchService searchService;
	
	@Autowired
	private AnalysisService analysisService;
	
	
	@ResponseBody
	@RequestMapping(value = "/scan/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
	public byte[] serveImage(@PathVariable String id) throws IOException {
	    return scanRepo.findById(id.replaceAll("\\.\\w+$", "")).getImage();
	}
	
	
	@ResponseBody
	@RequestMapping(value = "/actions")
	public String actions(@RequestParam(required = false) String action) throws IOException {
		if (action == null || action.length() == 0)
			return "";
		
		String result = "";
		
		if (action.equalsIgnoreCase("index")){
			result = searchService.createIndex();
		} else if (action.equalsIgnoreCase("create-semantic-data")){
			result = analysisService.createSimilarityData();
		} else if (action.equalsIgnoreCase("languages")){
			//ALL LANGUAGES OUTPUT
			Map<String, Integer> count = new HashMap<String, Integer>();
			for (PageDocument page : pageRepo.findAll()){
				for (String l : page.getLanguages()){
					if (count.containsKey(l)){
						count.put(l, count.get(l)+1);
					} else {
						count.put(l, 1);
					}
				}
			}
			result = count.toString();
		} else if (action.equalsIgnoreCase("idf")){
			//IDF TEST
			Map<String, Double> idf = IDF.idf(pageRepo.findByLanguages(new String[]{"Vallader"}));
			idf = CollectionTools.sortMapByValue(idf, false);
			for (String key : idf.keySet()){
				result += key + " (" + idf.get(key) + ")<br>";
			}
		}
		
	    return result;
	}
	
	
}
