package com.CityUIndoorNavigation.server.data;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LocationResponse {
    public LocationResponse(String nearestNode, int xCoordinate2, int yCoordinate2) {
		this.nodeId = nearestNode;
		this.xCoordinate = xCoordinate2;
		this.yCoordinate = yCoordinate2;
	}
	private String nodeId;
    private int xCoordinate;
    private int yCoordinate;
}
