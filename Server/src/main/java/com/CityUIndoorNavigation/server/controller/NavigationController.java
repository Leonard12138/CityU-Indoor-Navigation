package com.CityUIndoorNavigation.server.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.CityUIndoorNavigation.server.data.Node;
import com.CityUIndoorNavigation.server.service.NavigationService;

@RestController
@RequestMapping("/navigation")
public class NavigationController {

    @Autowired
    private NavigationService navigationService;

    @PostMapping("/navigate")
    public ResponseEntity<?> navigate(@RequestParam String startNodeId, @RequestParam String destinationRoomName) {
        Node startNode = navigationService.getNodeById(startNodeId);
        String destinationNodeId = navigationService.getNodeIdByRoomName(destinationRoomName);

        if (startNode == null || destinationNodeId == null) {
            return ResponseEntity.badRequest().body("Invalid start node or destination room name");
        }

        List<Map<String, Object>> path = navigationService.findPath(startNode.getId(), destinationNodeId);
        if (path.isEmpty()) {
            return ResponseEntity.badRequest().body("Path not found");
        }

        return ResponseEntity.ok(path);
    }
}
