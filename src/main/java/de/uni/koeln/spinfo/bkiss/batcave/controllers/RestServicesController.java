package de.uni.koeln.spinfo.bkiss.batcave.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.uni.koeln.spinfo.bkiss.batcave.data.DocumentRepository;

@RestController
public class RestServicesController {
	
	@Autowired
	private DocumentRepository repo;
	
	@RequestMapping("/search")
    public String search() {
        return "nothing here.";
    }
	
	
	
//	@RequestMapping(value = "/doc/{title}", method = RequestMethod.GET)
//    public String doc(@PathVariable String title) {
//		Document doc = repo.findByTitle(title);
//        return doc == null ? "{}" : doc.toString();
//    }
	
}
