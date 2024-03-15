package com.CityUIndoorNavigation.server.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.CityUIndoorNavigation.server.data.Node;

@Service
public interface NavigationService {

	void addNode(Node node);

	Node getNodeById(String id);
	
	String getNodeIdByRoomName(String roomName);

}
