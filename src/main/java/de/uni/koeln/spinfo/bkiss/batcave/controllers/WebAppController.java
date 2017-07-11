package de.uni.koeln.spinfo.bkiss.batcave.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.uni.koeln.spinfo.bkiss.batcave.analysis.AnalysisService;
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
	private AnalysisService analysisService;
	
	@Autowired
	private SearchService searchService;
	

	/**
	 * Default request handler
	 * @return
	 */
    @RequestMapping("*")
    public String defaultRequest() {
        return "search";
    }
    
    
    /**
     * Handles requests for system maintenance page
     * @param model
     * @return
     */
    @RequestMapping("/system")
    public String action(Model model) {
    	List<ControlsAction> actions = new ArrayList<ControlsAction>();
    	
    	actions.add(new ControlsAction("index", "Such-Index erstellen / aktualisieren"));
    	actions.add(new ControlsAction("create-semantic-data", "Analyse-Daten erzeugen"));
    	
    	model.addAttribute("actions", actions);
        return "system";
    }
    
    
    /**
     * Handles requests for search results page
     * @param search
     * @param token
     * @param tag
     * @param tagPrev
     * @param tagNext
     * @param mode
     * @param model
     * @param request
     * @return
     */
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
    	
    	//page title
    	model.addAttribute("title", "Suchergebnisse für \"" + token + "\"");
    	
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
    	
    	//analysis results
    	model.addAttribute("sims", analysisService.sims(token, 10));
    	
        return "search";
    }
    
    
    /**
     * Handles requests for semantic similarities search page
     * @param token
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/search-similarities")
    public String searchSimView(
    		@RequestParam String token,
    		Model model,
    		HttpServletRequest request) {
    	
    	//param defaults
    	if (token == null) return "search";
    	
    	//page title
    	model.addAttribute("title", "Semantische Ähnlichkeit zu \"" + token + "\"");
    	
    	//update session
    	updateSession(token, request);
    	
    	//analysis results
    	token = AnalysisService.cleanToken(token);
    	model.addAttribute("sims", analysisService.sims(token, 10));
    	model.addAttribute("token", token);
    	
        return "search-similarities";
    }
    
    
    /**
     * Handles requests for single document page view
     * @param id
     * @param hl
     * @param model
     * @return
     */
    @RequestMapping("/page")
    public String pageView(
    		@RequestParam(required = false) String id,
    		@RequestParam(required = false) String hl,
    		Model model) {
    	
    	if (id == null)
    		return "search";
    	
    	PageDocument page = pageRepo.findById(id);
    	//add page object
    	model.addAttribute("page", page);
    	//add tags array
    	model.addAttribute("tags", extractTags(page));
    	//add highlight index
   		model.addAttribute("highlight", hl != null ? Integer.valueOf(hl) : -1);
   		//guess page language
   		model.addAttribute("language", page.getLanguages().toArray()[0]);
    	
        return "page";
    }
    
    /**
     * Handles requests for semantic similarity view inside page view
     * @param word
     * @param language
     * @param model
     * @return
     */
    @RequestMapping("/similarity")
    public String similaritiesView(
    		@RequestParam String word,
    		@RequestParam(required = false) String language,
    		Model model) {
    	
    	word = AnalysisService.cleanToken(word);
    	model.addAttribute("word", word);
    	model.addAttribute("language", language);
    	model.addAttribute(
    			"similarities",
    			analysisService.sims(word, language, 10));
    	
        return "similarities";
    }
    
    /*
     * Helper method to extract all tags from a document
     */
    private Set<String> extractTags(PageDocument doc){
    	Set<String> tags = new HashSet<String>();
    	for (Token t : doc.getTokens())
    		tags.addAll(t.getTags());
    	return tags;
    }
    
    /*
     * Updates session data
     */
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
    
    /*
     * Updates session data
     */
    private void updateSession(String searchToken,
    		HttpServletRequest request){
    	request.getSession().setAttribute("searchToken", searchToken);
    }
    
}