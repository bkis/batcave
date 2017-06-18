package de.uni.koeln.spinfo.bkiss.batcave.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.uni.koeln.spinfo.bkiss.batcave.db.data.PageDocument;
import de.uni.koeln.spinfo.bkiss.batcave.db.data.PageDocumentRepository;
import de.uni.koeln.spinfo.bkiss.batcave.db.data.Token;
import de.uni.koeln.spinfo.bkiss.batcave.search.SearchResult;
import de.uni.koeln.spinfo.bkiss.batcave.search.SearchService;

@Controller
public class WebAppController {
	
	@Autowired
	private PageDocumentRepository pageRepo;
	
	@Autowired
	private SearchService searchService;
	

    @RequestMapping("*")
    public String defaultRequest() {
        return "search";
    }
    
    
    @RequestMapping("/action/{id}")
    public String action(@PathVariable String id, Model model) {
    	List<String> actions = new ArrayList<String>(Arrays.asList(id.split("\\+")));
    	
    	for (String action : actions){
    		if (action.equalsIgnoreCase("index")){
        		System.out.println(searchService.createIndex(pageRepo.findAll()));
        	} else if (action.equalsIgnoreCase("reindex")){
        		System.out.println(searchService.updateIndex(pageRepo.findAll()));
        	}
    	}
    	
    	model.addAttribute("actions", actions);
        return "action";
    }
    
    
    @RequestMapping("/search")
    public String searchView(
    		@RequestParam(required = false) String token,
    		@RequestParam(required = false) String tag,
    		@RequestParam(required = false) String tagPrev,
    		@RequestParam(required = false) String tagNext,
    		@RequestParam(required = false) String mode,
    		Model model,
    		HttpServletRequest request) {
    	
    	//param defaults
    	if ((token == null || token.length() == 0)
    			&& (tag == null || tag.length() == 0)) return "search";
    	if (token == null) token = "";
    	if (tag == null) tag = "";
    	if (mode == null) mode = "normal";
    	token = token.replaceAll("\\P{L}", "");
    	
    	//update session
    	updateSession(token, tag, tagPrev, tagNext, mode, request);
    	
    	//apply fuzzy search
    	if (mode.equalsIgnoreCase("fuzzy"))
    		token = token.concat("~");
    	
    	//search docs
    	List<SearchResult> results = searchService.search(token, tag, tagPrev, tagNext, 3);
    	
    	//results
    	model.addAttribute("results", results);
    	model.addAttribute("resultsCount", results.size());
    	
        return "search";
    }
    
    
    @RequestMapping("/page")
    public String pageView(
    		@RequestParam(required = false) String id,
    		@RequestParam(required = false) String hl,
    		Model model) {
    	
    	if (id == null)
    		return pageRndView(model);
    	
    	PageDocument page = pageRepo.findById(id);
    	//add page object
    	model.addAttribute("page", page);
    	//add tags array
    	model.addAttribute("tags", extractTags(page));
    	//add highlight index
   		model.addAttribute("highlight", hl != null ? Integer.valueOf(hl) : -1);
    	
        return "page";
    }
    
    @RequestMapping(value={"/page/"})
    public String pageRndView(Model model) {
    	
    	PageDocument page = pageRepo.findByVolume("Band I");
    	//add page object
    	model.addAttribute("page", page);
    	//add tags array
    	model.addAttribute("tags", extractTags(page));
        return "page";
    }
    
    
    private Set<String> extractTags(PageDocument doc){
    	Set<String> tags = new HashSet<String>();
    	for (Token t : doc.getTokens())
    		tags.addAll(t.getTags());
    	return tags;
    }
    
    
    private void updateSession(String searchToken,
    		String searchTag,
    		String searchTagPrev,
    		String searchTagNext,
    		String searchMode,
    		HttpServletRequest request){
    	
    	request.getSession().setAttribute("searchToken", searchToken);
    	request.getSession().setAttribute("searchTag", searchTag);
    	request.getSession().setAttribute("searchTagPrev", searchTagPrev);
    	request.getSession().setAttribute("searchTagNext", searchTagNext);
    	request.getSession().setAttribute("searchMode", searchMode);
    }
    
}