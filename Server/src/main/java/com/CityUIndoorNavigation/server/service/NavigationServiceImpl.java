package com.CityUIndoorNavigation.server.service;

import java.util.*;

import org.springframework.stereotype.Service;

import com.CityUIndoorNavigation.server.data.Node;

@Service
public class NavigationServiceImpl implements NavigationService {

    
    @Override
    public Node getNodeById(String id) {
        return Node.getNodeById(id);
    }


//    @Override
//    public void addNode(Node node) {
//        nodes.add(node);
//    }


    @Override
    public String getNodeIdByRoomName(String roomName) {
        if (roomName == null) return null;

        // Normalize the input: remove hyphens and convert to lower case
        String normalizedInput = roomName.replace("-", "").toLowerCase();

        for (Node node : Node.getAllNodes().values()) {
            if (node.getRoomName() != null) {
                // Normalize the room name of the node: remove hyphens and convert to lower case
                String normalizedNodeName = node.getRoomName().replace("-", "").toLowerCase();
                
                // Check if the normalized names are equal
                if (normalizedNodeName.equals(normalizedInput)) {
                    return node.getId(); // Return the ID of the first matching node
                }
            }
        }
        return null; // Return null if no matching node is found
    }



    @Override
    public List<Map<String, Object>> findPath(String startId, String destinationId) {
        Node startNode = getNodeById(startId);
        Node destinationNode = getNodeById(destinationId);

        List<Map<String, Object>> pathDetails = new ArrayList<>();

        if (startNode == null || destinationNode == null) {
            return pathDetails; // Return empty list if start or destination node is invalid
        }

        Queue<Node> queue = new LinkedList<>();
        Map<Node, Node> previousNode = new HashMap<>();
        Set<Node> visited = new HashSet<>();

        queue.add(startNode);
        visited.add(startNode);
        previousNode.put(startNode, null);

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

        if (!visited.contains(destinationNode)) {
            return pathDetails; // Return empty list if path is not found
        }

        for (Node node = destinationNode; node != null; node = previousNode.get(node)) {
            pathDetails.add(0, mapNodeDetails(node));
        }

        return pathDetails;
    }

    private Map<String, Object> mapNodeDetails(Node node) {
        Map<String, Object> nodeDetails = new HashMap<>();
        nodeDetails.put("nodeId", node.getId());
        nodeDetails.put("xCoordinate", node.getX());
        nodeDetails.put("yCoordinate", node.getY());
        return nodeDetails;
    }






}
