package de.uni.koeln.spinfo.bkiss.batcave.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jayway.jsonpath.JsonPath;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

import de.uni.koeln.spinfo.bkiss.batcave.db.data.PageDocument;
import de.uni.koeln.spinfo.bkiss.batcave.db.data.PageDocumentRepository;
import de.uni.koeln.spinfo.bkiss.batcave.db.data.Token;
import net.minidev.json.JSONArray;

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
    
    @RequestMapping("/rndpage")
    public String rndPageView(Model model) {
    	PageDocument page = pageRepo.findById("5622c29eef86d3c2f23fd63f");
    	System.out.println("REPO COUNT: " + pageRepo.count());
    	System.out.println("PAGE: " + page);
    	//add page id
    	model.addAttribute("id", page.getPageId());
    	//add page object
    	model.addAttribute("page", page);
    	//add page title string
    	model.addAttribute("title", "Seitenansicht");
    	//add tags array
    	model.addAttribute("tags", extractTags(page));
        return "page";
    }
    
    @RequestMapping("/page")
    public String pageView(
    		@RequestParam(value="id", required=true, defaultValue="error") String id,
    		Model model) {
    	
    	//load dummy data
    	Scanner fr = null;
    	try {
    		File file = new ClassPathResource("dummydata.json").getFile();
			fr = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	StringBuilder sb = new StringBuilder();
    	while (fr.hasNextLine())
    		sb.append(fr.nextLine() + "\n");
    	
    	fr.close();
    	
//    	Gson gson = new GsonBuilder().create();
//    	model.addAttribute("page", gson.fromJson(sb.toString(), PageDocument.class));
    	
    	BasicDBObject page = (BasicDBObject) JSON.parse(sb.toString());
    	
    	//add page id
    	model.addAttribute("id", id);
    	//add page object
    	model.addAttribute("page", page);
    	//add page title string
    	model.addAttribute("title", "Seitenansicht");
    	//add tags array
    	JSONArray tags = JsonPath.read(page, "$.tokens[*].tags[0]");
    	model.addAttribute("tags", new HashSet<String>(Arrays.asList(tags.toArray(new String[0]))));
    	
        return "page";
    }
    
    
    private String[] extractTags(PageDocument doc){
    	Set<String> tags = new HashSet<String>();
    	for (Token t : doc.getTokens())
    		tags.addAll(t.getTags());
    	return tags.toArray(new String[0]);
    }
    
    
}