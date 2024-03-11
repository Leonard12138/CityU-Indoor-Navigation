package com.CityUIndoorNavigation.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.CityUIndoorNavigation.server.controller.LocateDataPickController;
import com.CityUIndoorNavigation.server.data.WifiData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class indoorLocateServiceImpl implements indoorLocateService{
	
    @Autowired
    protected LocateDataService locateDataService;

	@Override
	public String locateUser(List<WifiData> wifiDataList) {
		
	      try {
	      log.info("Fetching all WiFi Data!");
	      
	      // Retrieve all WiFi data using the service
	      List<WifiData> wifiFingerprint = locateDataService.getWififingerprint();
	
	      // Return the retrieved data
	      return null;
	  } catch (Exception e) {
	      // Handle exceptions and return an error response
	      log.error("Error fetching all WiFi Data: " + e.getMessage());
	  }
		// TODO Auto-generated method stub
		return null;
	}



}
