package de.uni.koeln.spinfo.bkiss.batcave.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import de.uni.koeln.spinfo.bkiss.batcave.db.data.PageDocumentRepository;
import de.uni.koeln.spinfo.bkiss.batcave.db.data.ScanDocumentRepository;
import de.uni.koeln.spinfo.bkiss.batcave.search.SearchService;


@RestController
public class RestServicesController {
	
	@Autowired
	private PageDocumentRepository pageRepo;
	
	@Autowired
	ScanDocumentRepository scanRepo;
	
	@Autowired
	private SearchService searchService;
	
	
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
			result = searchService.createIndex(pageRepo.findAll());
		} else if (action.equalsIgnoreCase("wait")){
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			result = "OK, jetzt ist mir langweilig.";
		} else if (action.equalsIgnoreCase("create-vspace-tag-neighbors")){
			
		}
		
	    return result;
	}
	
	
//	@RequestMapping(value = "/doc/{title}", method = RequestMethod.GET)
//    public String doc(@PathVariable String title) {
//		Document doc = repo.findByTitle(title);
//        return doc == null ? "{}" : doc.toString();
//    }
	
}
