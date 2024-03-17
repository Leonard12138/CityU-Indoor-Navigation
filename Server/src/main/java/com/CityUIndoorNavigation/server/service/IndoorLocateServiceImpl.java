package com.CityUIndoorNavigation.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.CityUIndoorNavigation.server.data.LocationResponse;
import com.CityUIndoorNavigation.server.data.WifiData;
import com.CityUIndoorNavigation.server.repository.WifiDataRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IndoorLocateServiceImpl implements IndoorLocateService {

    @Autowired
    private LocateDataService locateDataService;

    @Autowired
    private WifiDataRepository wifiDataRepository;

    // Distance between nodes in meters
    private static final double NODE_DISTANCE_METERS = 4.0;
    // Corresponding pixel distance in XY coordinates
    private static final double NODE_DISTANCE_PIXELS = 195.0;

    @Override
    public LocationResponse locateUser(List<WifiData> wifiDataList) {
        try {
            // Retrieve all WiFi fingerprint data
            List<WifiData> wifiFingerprint = locateDataService.getWififingerprint();

            // Implement k-NN algorithm to find the nearest node
            String nearestNode = findNearestNeighbor(wifiDataList, wifiFingerprint);

            // Fetch XY coordinates based on the nearest node ID
            List<Object[]> coordinates = wifiDataRepository.findDistinctCoordinatesByNodeId(nearestNode);

            if (!coordinates.isEmpty()) {
                int xCoordinate = (int) coordinates.get(0)[0];
                int yCoordinate = (int) coordinates.get(0)[1];

                return new LocationResponse(nearestNode, xCoordinate, yCoordinate);
            } else {
                log.error("Coordinates not found for Node ID: {}", nearestNode);
                return null;
            }
        } catch (Exception e) {
            log.error("Error locating user: {}", e.getMessage());
            return null;
        }
    }

    private String findNearestNeighbor(List<WifiData> wifiDataList, List<WifiData> wifiFingerprint) {
        // Sort the wifiDataList by signal strength and select the top 5 strongest signals
        List<WifiData> strongestSignals = wifiDataList.stream()
                                                      .sorted(Comparator.comparing(WifiData::getLevel).reversed())
                                                      .limit(5)
                                                      .collect(Collectors.toList());

        Map<String, Double> nodeIdToWeightedSum = new HashMap<>();

        for (WifiData realTimeWifiData : strongestSignals) {
            for (WifiData fingerprintData : wifiFingerprint) {
                double distance = calculateEuclideanDistance(realTimeWifiData, fingerprintData);
                double weight = 1.0 / (distance + 1); // Adding 1 to avoid division by zero

                nodeIdToWeightedSum.merge(fingerprintData.getNodeId(), weight, Double::sum);
            }
        }

        return nodeIdToWeightedSum.entrySet().stream()
                                  .max(Map.Entry.comparingByValue())
                                  .map(Map.Entry::getKey)
                                  .orElse(null);
    }

    private double calculateEuclideanDistance(WifiData wifiData1, WifiData wifiData2) {
        double signalStrengthDiff = wifiData1.getLevel() - wifiData2.getLevel();
        return Math.abs(signalStrengthDiff);
    }

}
