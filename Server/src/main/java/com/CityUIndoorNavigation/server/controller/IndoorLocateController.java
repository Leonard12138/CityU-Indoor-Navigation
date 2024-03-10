package com.CityUIndoorNavigation.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.CityUIndoorNavigation.server.data.WifiData;
import com.CityUIndoorNavigation.server.service.indoorLocateService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/indoorLocate")
public class IndoorLocateController {
    @Autowired
    protected indoorLocateService indoorLocateService;
    
    @PostMapping("/processWifiData")
    public ResponseEntity<String> pickWifiData(@RequestParam("type") String type, @ModelAttribute WifiData wifiData) {
    	
    	log.info(wifiData.toString());
    	

    	return ResponseEntity.ok("WifiData uploaded successfully");
        
    }
}
