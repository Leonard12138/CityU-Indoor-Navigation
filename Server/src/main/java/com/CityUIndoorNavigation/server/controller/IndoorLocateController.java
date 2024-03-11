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
    public ResponseEntity<String> processWifiData(@RequestParam("type") String type, @ModelAttribute WifiData wifiData) {
    	
        try {
            log.info("Processing WiFi Data!");
            log.info(wifiData.toString());

            // Call the service to process and locate the user
            String nearestNode = indoorLocateService.locateUser(wifiData);

            // Return the nearest node information to the user
            return ResponseEntity.ok("Nearest Node: " + nearestNode);
        } catch (Exception e) {
            // Handle exceptions and return an error response
            log.error("Error processing WiFi Data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing WiFi Data: " + e.getMessage());
        }
        
    }
}
