package de.uni.koeln.spinfo.bkiss.batcave.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebAppController {

    @RequestMapping("/")
    public String greeting() {
        return "index";
    }
    
    @RequestMapping("*")
    public String defaultRequest() {
        return "index";
    }

}