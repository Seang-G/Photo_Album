package com.squarecross.photoalbum.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class TestController {


    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public ResponseEntity<String> test() {
        return new ResponseEntity<>("Hello!", HttpStatus.OK);
    }
}
