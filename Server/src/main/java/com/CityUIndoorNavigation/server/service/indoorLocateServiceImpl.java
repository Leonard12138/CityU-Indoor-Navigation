package com.CityUIndoorNavigation.server.service;

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
            String nearestNode = findNearestNeighbor(wifiDataList, wifiFingerprint);

            log.info("User located at node: {}", nearestNode);

            return nearestNode;
        } catch (Exception e) {
            log.error("Error locating user: {}", e.getMessage());
            return null;
            }
        }

    private String findNearestNeighbor(List<WifiData> wifiDataList, List<WifiData> wifiFingerprint) {
        // Iterate through each real-time scanned Wi-Fi data
        for (WifiData realTimeWifiData : wifiDataList) {
            // Find the nearest neighbor based on Euclidean distances
            WifiData nearestNeighbor = wifiFingerprint.stream()
                    .min(Comparator.comparingDouble(fingerprint ->
                            calculateEuclideanDistance(realTimeWifiData, fingerprint)))
                    .orElse(null);

            // Return the ID of the nearest node
            return nearestNeighbor.getNode_id();
        }

        return null;
    }

    private double calculateEuclideanDistance(WifiData wifiData1, WifiData wifiData2) {
        // Calculate Euclidean distance between two points in a 2D space
        double deltaX = wifiData1.getX() - wifiData2.getX();
        double deltaY = wifiData1.getY() - wifiData2.getY();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

}
