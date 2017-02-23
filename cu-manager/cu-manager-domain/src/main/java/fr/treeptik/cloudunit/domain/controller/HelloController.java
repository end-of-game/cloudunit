package fr.treeptik.cloudunit.domain.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {
    
    @GetMapping
    public String hello() {
        return "Hello, Donald! S:-]";
    }
}
