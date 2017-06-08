package de.uni.koeln.spinfo.bkiss.batcave.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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
	

    @RequestMapping("/")
    public String index() {
        return "index";
    }
    
    @RequestMapping("*")
    public String defaultRequest() {
        return "index";
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
    		@RequestParam(required = false) String mode,
    		Model model) {
    	
    	if (token == null && tag == null) return "search";
    	if (tag == null) tag = "";
    	if (mode == null) mode = "normal";
    	
    	//search state
    	model.addAttribute("searchToken", token);
    	model.addAttribute("searchTag", tag);
    	model.addAttribute("searchMode", mode);
    	
    	//apply fuzzy search
    	if (mode.equalsIgnoreCase("fuzzy"))
    		token = token + "~";
    	
    	//search docs
    	List<SearchResult> results = searchService.search(token, tag, 3);
    	
    	//results
    	model.addAttribute("results", results);
    	
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
    
    
}