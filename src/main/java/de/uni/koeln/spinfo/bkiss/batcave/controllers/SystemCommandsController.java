package de.uni.koeln.spinfo.bkiss.batcave.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import de.uni.koeln.spinfo.bkiss.batcave.analysis.AnalysisService;
import de.uni.koeln.spinfo.bkiss.batcave.db.data.PageDocument;
import de.uni.koeln.spinfo.bkiss.batcave.db.data.PageDocumentRepository;
import de.uni.koeln.spinfo.bkiss.batcave.search.SearchService;


/**
 * Controller class for system commands/maintenance
 * @author kiss
 *
 */
@RestController
public class SystemCommandsController {
	
	@Autowired
	private PageDocumentRepository pageRepo;
	
	@Autowired
	private SearchService searchService;
	
	@Autowired
	private AnalysisService analysisService;
	
	
	
	/**
	 * Handles system maintenance command requests
	 * @param action
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "/actions")
	public String actions(@RequestParam(required = false) String action) throws IOException {
		if (action == null || action.length() == 0)
			return "";
		
		String result = "";
		
		if (action.equalsIgnoreCase("index")){							//create index
			result = searchService.createIndex();
		} else if (action.equalsIgnoreCase("create-semantic-data")){	//create similarity data
			result = analysisService.createSimilarityData();
		} else if (action.equalsIgnoreCase("languages")){				//list all languages contained in corpus
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
		}
		
	    return result;
	}
	
	
}
