package com.CityUIndoorNavigation.server.data;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Node {
    private String id;
    private String roomName; // If representing a room
    private int x;
    private int y;
}
