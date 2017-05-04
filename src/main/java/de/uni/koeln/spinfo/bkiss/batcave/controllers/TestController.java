package de.uni.koeln.spinfo.bkiss.batcave.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.uni.koeln.spinfo.bkiss.batcave.data.Document;
import de.uni.koeln.spinfo.bkiss.batcave.data.DocumentRepository;

@RestController
public class TestController {
	
	@Autowired
	private DocumentRepository repo;
	
	@RequestMapping("/")
    public String index() {
        return "Die Menschen brauchen Erziehung, nicht Unterhaltung!";
    }
	
	@RequestMapping("/docs")
    public String docs() {
        return repo.findAll().toString();
    }
	
	@RequestMapping(value = "/doc/{title}", method = RequestMethod.GET)
    public String doc(@PathVariable String title) {
		Document doc = repo.findByTitle(title);
        return doc == null ? "{}" : doc.toString();
    }
	
}
