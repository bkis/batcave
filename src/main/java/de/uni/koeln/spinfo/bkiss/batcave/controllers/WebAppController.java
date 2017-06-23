package de.uni.koeln.spinfo.bkiss.batcave.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.uni.koeln.spinfo.bkiss.batcave.db.data.ControlsAction;
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
    
    
    @RequestMapping("/system")
    public String action(Model model) {
    	List<ControlsAction> actions = new ArrayList<ControlsAction>();
    	
    	actions.add(new ControlsAction("index", "Such-Index erstellen / aktualisieren"));
    	actions.add(new ControlsAction("load-vspace-tag-neighbors", "Vektor-Raum \"benachbarte Annotationen\" laden"));
    	actions.add(new ControlsAction("create-vspace-tag-neighbors", "Vektor-Raum \"benachbarte Annotationen\" neu erstellen"));
    	actions.add(new ControlsAction("wait", "Warte mal kurz!"));
    	
    	model.addAttribute("actions", actions);
        return "system";
    }
    
    
    @RequestMapping("/search")
    public String searchView(
    		@RequestParam(required = false) String search,
    		@RequestParam(required = false) String token,
    		@RequestParam(required = false) String tag,
    		@RequestParam(required = false) String tagPrev,
    		@RequestParam(required = false) String tagNext,
    		@RequestParam(required = false) String mode,
    		Model model,
    		HttpServletRequest request) {
    	
    	//param defaults
    	if (search == null || !search.equalsIgnoreCase("true"))
    		return "search";
    	if (token == null) token = "";
    	if (tag == null) tag = "";
    	if (tagPrev == null) tagPrev = "";
    	if (tagNext == null) tagNext = "";
    	if (mode == null) mode = "normal";
    	token = token.replaceAll("\\P{L}", "");
    	
    	//update session
    	updateSession(token, tag, tagPrev, tagNext, mode, request);
    	
    	//search docs
    	List<SearchResult> results = searchService.search(
    			token, 
    			tag, 
    			tagPrev, 
    			tagNext, 
    			mode.equalsIgnoreCase("fuzzy"), 
    			3);
    	
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