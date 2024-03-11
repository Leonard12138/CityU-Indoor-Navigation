package com.CityUIndoorNavigation.server.service;

import java.util.ArrayList;
import java.util.Comparator;
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
            log.info("Locating user...");

            // Retrieve all WiFi fingerprint data
            List<WifiData> wifiFingerprint = locateDataService.getWififingerprint();

            // Implement k-NN algorithm to find the nearest node
            String nearestNode = kNN(wifiDataList, wifiFingerprint);

            log.info("User located at node: {}", nearestNode);

            return nearestNode;
        } catch (Exception e) {
            log.error("Error locating user: {}", e.getMessage());
            return null;
            }
        }

	private String kNN(List<WifiData> wifiDataList, List<WifiData> wifiFingerprint) {
	    int k = 3;  // Adjust the value of k as needed

	    // Iterate through each real-time scanned Wi-Fi data
	    for (WifiData realTimeWifiData : wifiDataList) {
	        // Sort Wi-Fi fingerprints based on the absolute difference with RSSI values
	        wifiFingerprint.sort(Comparator.comparingDouble(fingerprint ->
	                Math.abs(realTimeWifiData.getLevel() - fingerprint.getLevel())));

	        // Take the top k neighbors
	        List<WifiData> nearestNeighbors = wifiFingerprint.subList(0, k);

	        // Perform any additional processing based on the nearest neighbors
	        // (e.g., voting mechanism to determine the final location)

	        // For simplicity, return the node ID of the closest neighbor
	        return nearestNeighbors.get(0).getNode_id();
	    }

	    return null;
	}
}
