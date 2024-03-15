package com.CityUIndoorNavigation.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.CityUIndoorNavigation.server.data.Node;
import com.CityUIndoorNavigation.server.service.NavigationService;

import java.util.Optional;

@RestController
@RequestMapping("/navigation")
public class NavigationController {
    @Autowired
    private NavigationService navigationService;

    @PostMapping
    public void addNode(@RequestBody Node node) {
    	navigationService.addNode(node);
    }

    @GetMapping("/{id}")
    public Node getNode(@PathVariable String id) {
        return navigationService.getNodeById(id);
    }

    @GetMapping("/room/{roomName}")
    public Node getNodeByRoomName(@PathVariable String roomName) {
        String nodeId = navigationService.getNodeIdByRoomName(roomName);
        if (!nodeId.isEmpty()) {
            return navigationService.getNodeById(nodeId);
        } else {
            return null;
        }
    }
}
