package com.MediConnect.MediConnect.Controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MediConnectController {
    @GetMapping("/get")
    public String getApiStatus() {
        return "API Working";
    }
}
