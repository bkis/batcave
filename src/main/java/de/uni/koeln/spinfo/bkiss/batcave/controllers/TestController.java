package de.uni.koeln.spinfo.bkiss.batcave.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
	
	@RequestMapping("/")
    public String index() {
        return "Die Menschen brauchen Erziehung, nicht Unterhaltung!";
    }
	
	@RequestMapping(value = "/upper/{text}/{text2}", method = RequestMethod.GET)
    public String upper(@PathVariable String text,
    				   @PathVariable String text2) {
        return text.toUpperCase() + "\n" + text2.toUpperCase();
    }
	
}
