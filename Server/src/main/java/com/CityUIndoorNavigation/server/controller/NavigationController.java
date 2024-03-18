package com.CityUIndoorNavigation.server.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.CityUIndoorNavigation.server.data.Node;
import com.CityUIndoorNavigation.server.service.NavigationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/navigation")
public class NavigationController {

    @Autowired
    private NavigationService navigationService;

    @PostMapping("/navigate")
    public ResponseEntity<?> navigate(@RequestBody Map<String, String> requestBody) {
        String startNodeId = requestBody.get("startNodeId");
        String destinationRoomName = requestBody.get("destinationRoomName");        
        
        log.info("Received navigation request. Start node ID: {}, Destination room name: {}", startNodeId, destinationRoomName);
        
        Node startNode = navigationService.getNodeById(startNodeId);

        String destinationNodeId = navigationService.getNodeIdByRoomName(destinationRoomName);

        if (startNode == null || destinationNodeId == null) {
            log.info("Invalid start node or destination room name Start node ID: {}, Destination ndoe id: {}", startNodeId, destinationNodeId);
            return ResponseEntity.badRequest().body("Invalid start node or destination room name");
        }

        List<Map<String, Object>> path = navigationService.findPath(startNode.getId(), destinationNodeId);
        if (path.isEmpty()) {
        	log.info("Path not found");
            return ResponseEntity.badRequest().body("Path not found");
        }
        
        // Logging the path for debugging
        logPathDetails(path);
        
        return ResponseEntity.ok(path);
    }

    private void logPathDetails(List<Map<String, Object>> path) {
        if (path.isEmpty()) {
            log.info("No path found.");
            return;
        }

        // Initialize StringBuilder to accumulate the path sequence
        StringBuilder pathSequence = new StringBuilder();
        
        // Log the total number of steps in the path
        log.info("Path found with {} steps:", path.size());
        
        // Iterate through the path steps
        for (int i = 0; i < path.size(); i++) {
            Map<String, Object> step = path.get(i);
            
            // Append the current node ID to the path sequence
            if (i > 0) { // Add the separator after the first element
                pathSequence.append(" -> ");
            }
            pathSequence.append(step.get("nodeId"));
            
            // Optionally log the detailed information of each step
            log.info("Node ID: {}, X: {}, Y: {}", step.get("nodeId"), step.get("xCoordinate"), step.get("yCoordinate"));
        }
        
        // Log the complete path sequence
        log.info("Path sequence: {}", pathSequence.toString());
    }

}
