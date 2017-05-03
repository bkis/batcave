package de.uni.koeln.spinfo.bkiss.batcave.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
	
	@RequestMapping("/")
    public String index() {
        return "Die Menschen brauchen Erziehung, nicht Unterhaltung!";
    }

}
