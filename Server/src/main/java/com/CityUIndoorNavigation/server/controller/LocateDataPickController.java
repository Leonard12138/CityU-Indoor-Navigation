package com.CityUIndoorNavigation.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.CityUIndoorNavigation.server.data.WifiData;
import com.CityUIndoorNavigation.server.service.LocateDataService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/locateDataPick")
public class LocateDataPickController {

    @Autowired
    protected LocateDataService locateDataService;

    @PostMapping("/uploadWifiData")
    public ResponseEntity<String> pickWifiData(@RequestParam("type") String type, @ModelAttribute WifiData wifiData) {
    	
        try {
        	log.info("Uploading Wifi Data!");
            // Save the list of WifiData
        	locateDataService.saveWifiData(wifiData);
			
            // Return a success response
            return ResponseEntity.ok("WifiData uploaded successfully");
        } catch (Exception e) {
            // Handle exceptions and return an error response
        	log.error("Error uploading WifiData: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading WifiData: " + e.getMessage());
        }
    }
    
    @GetMapping("/getAllWifiData")
    public ResponseEntity<List<WifiData>> getAllWifiData() {
        try {
            log.info("Fetching all WiFi Data!");
            
            // Retrieve all WiFi data using the service
            List<WifiData> wifiDataList = locateDataService.getWifiData();

            // Return the retrieved data
            return ResponseEntity.ok(wifiDataList);
        } catch (Exception e) {
            // Handle exceptions and return an error response
            log.error("Error fetching all WiFi Data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    
}