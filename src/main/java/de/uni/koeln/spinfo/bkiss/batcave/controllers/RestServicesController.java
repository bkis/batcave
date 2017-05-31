package de.uni.koeln.spinfo.bkiss.batcave.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import de.uni.koeln.spinfo.bkiss.batcave.db.data.ScanDocumentRepository;


@RestController
public class RestServicesController {
	
	@Autowired
	ScanDocumentRepository scanRepo;
	
	
	@RequestMapping("/search")
    public String search() {
        return "nothing here.";
    }
	
	@ResponseBody
	@RequestMapping(value = "/scan/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
	public byte[] serveImage(@PathVariable String id) throws IOException {
	    return scanRepo.findById(id.replaceAll("\\.\\w+$", "")).getImage();
	}
	
	
	
//	@RequestMapping(value = "/doc/{title}", method = RequestMethod.GET)
//    public String doc(@PathVariable String title) {
//		Document doc = repo.findByTitle(title);
//        return doc == null ? "{}" : doc.toString();
//    }
	
}
