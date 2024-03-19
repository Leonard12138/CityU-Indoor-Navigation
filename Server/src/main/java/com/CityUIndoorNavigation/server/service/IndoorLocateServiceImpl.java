package com.CityUIndoorNavigation.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.CityUIndoorNavigation.server.data.LocationResponse;
import com.CityUIndoorNavigation.server.data.Node;
import com.CityUIndoorNavigation.server.data.WifiData;
import com.CityUIndoorNavigation.server.repository.WifiDataRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IndoorLocateServiceImpl implements IndoorLocateService {

    @Autowired
    private LocateDataService locateDataService;

    @Autowired
    private WifiDataRepository wifiDataRepository;

    private LocationResponse lastLocation = null; // For smoothing
    private static final double SMOOTHING_FACTOR = 0.6; // Adjust as needed

    @Override
    public LocationResponse locateUser(List<WifiData> wifiDataList) {
        try {
            List<WifiData> wifiFingerprint = locateDataService.getWififingerprint();
            String nearestNode = findNearestNeighbor(wifiDataList, wifiFingerprint);

            List<Object[]> coordinates = wifiDataRepository.findDistinctCoordinatesByNodeId(nearestNode);
            if (!coordinates.isEmpty()) {
                int xCoordinate = (int) coordinates.get(0)[0];
                int yCoordinate = (int) coordinates.get(0)[1];

                // Apply smoothing
                if (lastLocation != null) {
                    xCoordinate = (int) (xCoordinate * SMOOTHING_FACTOR + lastLocation.getXCoordinate() * (1 - SMOOTHING_FACTOR));
                    yCoordinate = (int) (yCoordinate * SMOOTHING_FACTOR + lastLocation.getYCoordinate() * (1 - SMOOTHING_FACTOR));
                }

                // Snap to nearest node after smoothing
                LocationResponse smoothedLocation = new LocationResponse(nearestNode, xCoordinate, yCoordinate);
                LocationResponse finalLocation = snapToNearestNode(smoothedLocation);

                lastLocation = finalLocation; // Update last location with the snapped location
                return finalLocation;
            } else {
                log.error("Coordinates not found for Node ID: {}", nearestNode);
                return null;
            }
        } catch (Exception e) {
            log.error("Error locating user: {}", e.getMessage());
            return null;
        }
    }

    private LocationResponse snapToNearestNode(LocationResponse smoothedLocation) {
        // Placeholder for logic to find the nearest node to smoothedLocation
        // This would likely involve iterating over all nodes to find the one with minimum distance to smoothedLocation
        // and returning a new LocationResponse for the nearest node.
        // Example pseudocode (implementation depends on your data structures):
        double minDistance = Double.MAX_VALUE;
        String nearestNodeId = "";
        int nearestX = 0, nearestY = 0;
        for (Map.Entry<String, Node> entry : Node.getAllNodes().entrySet()) {
            Node node = entry.getValue();
            double distance = Math.sqrt(Math.pow(node.getX() - smoothedLocation.getXCoordinate(), 2) + Math.pow(node.getY() - smoothedLocation.getYCoordinate(), 2));
            if (distance < minDistance) {
                minDistance = distance;
                nearestNodeId = node.getId();
                nearestX = node.getX();
                nearestY = node.getY();
            }
        }
        return new LocationResponse(nearestNodeId, nearestX, nearestY);
    }


    private String findNearestNeighbor(List<WifiData> wifiDataList, List<WifiData> wifiFingerprint) {
        // Signal normalization could be implemented here
        Set<String> bssids = wifiDataList.stream().map(WifiData::getBssid).collect(Collectors.toSet());
        List<WifiData> filteredFingerprint = wifiFingerprint.stream().filter(data -> bssids.contains(data.getBssid())).collect(Collectors.toList());

        Map<String, Double> nodeIdToWeightedSum = new HashMap<>();
        for (WifiData realTimeWifiData : wifiDataList) {
            for (WifiData fingerprintData : filteredFingerprint) {
                if (realTimeWifiData.getBssid().equals(fingerprintData.getBssid())) {
                    double distance = calculateEuclideanDistance(realTimeWifiData, fingerprintData);
                    double weight = 1.0 / (Math.pow(distance, 2) + 1); // Adjusted for signal attenuation
                    nodeIdToWeightedSum.merge(fingerprintData.getNodeId(), weight, Double::sum);
                }
            }
        }

        return Collections.max(nodeIdToWeightedSum.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    private double calculateEuclideanDistance(WifiData wifiData1, WifiData wifiData2) {
        // Placeholder for normalization logic
        double signalStrengthDiff = wifiData1.getLevel() - wifiData2.getLevel();
        return Math.abs(signalStrengthDiff); // Consider applying a more sophisticated distance metric
    }
}
