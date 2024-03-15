package com.CityUIndoorNavigation.server.service;

import java.util.*;

import org.springframework.stereotype.Service;

import com.CityUIndoorNavigation.server.data.Node;

@Service
public class NavigationServiceImpl implements NavigationService {

    private Set<Node> nodes = new HashSet<>();

    @Override
    public void addNode(Node node) {
        nodes.add(node);
    }

    @Override
    public Node getNodeById(String id) {
        for (Node node : nodes) {
            if (node.getId().equals(id)) {
                return node;
            }
        }
        return null;
    }

    @Override
    public String getNodeIdByRoomName(String roomName) {
        for (Node node : nodes) {
            if (node.getRoomName() != null && node.getRoomName().equals(roomName)) {
                return node.getId();
            }
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> findPath(String startId, String destinationId) {
        Node startNode = getNodeById(startId);
        Node destinationNode = getNodeById(destinationId);

        List<Map<String, Object>> pathDetails = new ArrayList<>();

        if (startNode == null || destinationNode == null) {
            return pathDetails; // Return empty list if start or destination node is invalid
        }

        // Initialization
        Queue<Node> queue = new LinkedList<>();
        Map<Node, Node> previousNode = new HashMap<>();
        Set<Node> visited = new HashSet<>();

        // Setup
        queue.add(startNode);
        visited.add(startNode);
        previousNode.put(startNode, null);

        // BFS to find the shortest path
        while (!queue.isEmpty()) {
            Node currentNode = queue.remove();
            if (currentNode.equals(destinationNode)) {
                break;
            }

            for (Node neighbor : currentNode.getNeighbors()) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    previousNode.put(neighbor, currentNode);
                    queue.add(neighbor);
                }
            }
        }

        // Reconstruct the path and gather details
        if (!visited.contains(destinationNode)) {
            return pathDetails; // Return empty list if path is not found
        }

        Node node = destinationNode;
        while (node != null) {
            Map<String, Object> nodeDetails = new HashMap<>();
            nodeDetails.put("nodeId", node.getId());
            nodeDetails.put("xCoordinate", node.getX());
            nodeDetails.put("yCoordinate", node.getY());
            pathDetails.add(0, nodeDetails); // Add to the beginning to reverse the path order

            node = previousNode.get(node);
        }

        return pathDetails;
    }


    private Set<Node> getNeighbors(Node node) {
        return node.getNeighbors();
    }

}
