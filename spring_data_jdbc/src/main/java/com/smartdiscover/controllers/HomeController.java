package com.smartdiscover.controllers;

import com.smartdiscover.services.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @Autowired
    private LibraryService libraryService;

    @GetMapping("/")
    public void index() {
        libraryService.listBook();
    }
}
