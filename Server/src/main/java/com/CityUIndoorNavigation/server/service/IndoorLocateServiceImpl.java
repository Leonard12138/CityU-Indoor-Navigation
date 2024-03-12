package com.CityUIndoorNavigation.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.CityUIndoorNavigation.server.data.WifiData;
import com.CityUIndoorNavigation.server.repository.WifiDataRepository;
import lombok.extern.slf4j.Slf4j;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class IndoorLocateServiceImpl implements IndoorLocateService {

    @Autowired
    private LocateDataService locateDataService;

    @Autowired
    private WifiDataRepository wifiDataRepository;

    @Override
    public String locateUser(List<WifiData> wifiDataList) {
        try {
            log.info("Locating user...");

            // Retrieve all WiFi fingerprint data
            List<WifiData> wifiFingerprint = locateDataService.getWififingerprint();

            // Implement k-NN algorithm to find the nearest node
            String nearestNode = findNearestNeighbor(wifiDataList, wifiFingerprint);

            log.info("User located at node: {}", nearestNode);

            // Fetch XY coordinates based on the nearest node ID
            List<Object[]> coordinates = wifiDataRepository.findDistinctCoordinatesByNodeId(nearestNode);

            if (!coordinates.isEmpty()) {
                int xCoordinate =  (int) coordinates.get(0)[0];
                int yCoordinate = (int) coordinates.get(0)[1];

                log.info("Node ID: {}, X: {}, Y: {}", nearestNode, xCoordinate, yCoordinate);

                return "Nearest Node: " + nearestNode + ", X: " + xCoordinate + ", Y: " + yCoordinate;
            } else {
                log.error("Coordinates not found for Node ID: {}", nearestNode);
                return "Coordinates not found for Node ID: " + nearestNode;
            }
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
            return nearestNeighbor.getNodeId();
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
