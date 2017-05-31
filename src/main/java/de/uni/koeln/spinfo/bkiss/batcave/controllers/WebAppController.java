package de.uni.koeln.spinfo.bkiss.batcave.controllers;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import de.uni.koeln.spinfo.bkiss.batcave.db.data.PageDocument;
import de.uni.koeln.spinfo.bkiss.batcave.db.data.PageDocumentRepository;
import de.uni.koeln.spinfo.bkiss.batcave.db.data.Token;

@Controller
public class WebAppController {
	
	@Autowired
	private PageDocumentRepository pageRepo;
	

    @RequestMapping("/")
    public String index() {
        return "index";
    }
    
    @RequestMapping("*")
    public String defaultRequest() {
        return "index";
    }
    

    @RequestMapping("/page/{id}")
    public String pageView(
    		@PathVariable String id,
    		Model model) {
    	
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