package com.example.ecommerce.controllers;

import org.apache.http.protocol.HTTP;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/check")
public class CheckController {

    @GetMapping("/simple")
    public ResponseEntity check() {
        return new ResponseEntity("Check status ok!" , HttpStatus.OK);
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping("/logged")
    public ResponseEntity adminCheck() {
        return new ResponseEntity("admin status ok" , HttpStatus.OK);
    }

    @PreAuthorize("hasRole('User')")
    @GetMapping("/logged/user")
    public ResponseEntity userCheck() {
        return new ResponseEntity("user ok", HttpStatus.OK);
    }


}
