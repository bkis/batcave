package de.uni.koeln.spinfo.bkiss.batcave.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
import de.uni.koeln.spinfo.bkiss.batcave.search.SearchService;

@Controller
public class WebAppController {
	
	@Autowired
	private PageDocumentRepository pageRepo;
	
	@Autowired
	private SearchService lucene;
	

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
        		System.out.println(lucene.createIndex(pageRepo.findAll()));
        	} else if (action.equalsIgnoreCase("reindex")){
        		System.out.println(lucene.updateIndex(pageRepo.findAll()));
        	}
    	}
    	
    	model.addAttribute("actions", actions);
        return "action";
    }
    
    
    @RequestMapping("/search")
    public String searchView(
    		@RequestParam(required = false) String token,
    		@RequestParam(required = false) String tag,
    		Model model) {
    	
    	if (token == null && tag == null)
    		return "search";
    	//search docs
    	List<String> hits = lucene.search(token, tag, 100);
    	//get docs data from db
    	List<PageDocument> pages = new ArrayList<PageDocument>();
    	for (String hit : hits) pages.add(pageRepo.findOne(hit));
    	
    	model.addAttribute("results", pages);
    	
        return "search";
    }
    
    
    @RequestMapping("/page/{id}")
    public String pageView(@PathVariable String id,	Model model) {
    	
    	PageDocument page = pageRepo.findById(id);
    	//add page object
    	model.addAttribute("page", page);
    	//add tags array
    	model.addAttribute("tags", extractTags(page));
        return "page";
    }
    
    @RequestMapping(value={"/page/", "/page"})
    public String pageRndView(Model model) {
    	
    	PageDocument page = pageRepo.findByVolume("Band I");
    	//add page object
    	model.addAttribute("page", page);
    	//add tags array
    	model.addAttribute("tags", extractTags(page));
        return "page";
    }
    
    
    private String[] extractTags(PageDocument doc){
    	Set<String> tags = new HashSet<String>();
    	for (Token t : doc.getTokens())
    		tags.addAll(t.getTags());
    	return tags.toArray(new String[0]);
    }
    
    
}