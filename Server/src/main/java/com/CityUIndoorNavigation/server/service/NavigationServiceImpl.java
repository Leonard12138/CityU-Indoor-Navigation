package com.CityUIndoorNavigation.server.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.CityUIndoorNavigation.server.data.Node;

@Service
public class NavigationServiceImpl implements NavigationService{
    private final Map<String, Node> nodeMap = new HashMap<>();
    private final Map<String, String> roomToNodeIdMap = new HashMap<>();

	@Override
    public void addNode(Node node) {
        nodeMap.put(node.getId(), node);
        if (node.getRoomName() != null && !node.getRoomName().isEmpty()) {
            roomToNodeIdMap.put(node.getRoomName(), node.getId());
        }
    }

	@Override
	  public Node getNodeById(String id) {
        return nodeMap.get(id);
    }
	
    public String getNodeIdByRoomName(String roomName) {
        return roomToNodeIdMap.get(roomName);
    }
}
