package com.CityUIndoorNavigation.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CityUIndoorNavigation.server.data.LocationResponse;
import com.CityUIndoorNavigation.server.data.WifiData;
import com.CityUIndoorNavigation.server.service.IndoorLocateService;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/indoorLocate")
public class IndoorLocateController {

    @Autowired
    protected IndoorLocateService indoorLocateService;

    @PostMapping("/processWifiData")
    public ResponseEntity<LocationResponse> processWifiData(@RequestBody List<WifiData> wifiDataList) {

        try {
//            log.info("Processing WiFi Data!");

            // Call the service to process and locate the user
            // Note: You may need to modify the service to handle a list of WifiData
            LocationResponse locationResponse = indoorLocateService.locateUser(wifiDataList);
            log.info(locationResponse.toString());
            // Return the nearest node information to the user
            return ResponseEntity.ok(locationResponse);
        } catch (Exception e) {
            // Handle exceptions and return an error response
            log.error("Error processing WiFi Data: " + e.getMessage());
            return null;
        }
    }
}
