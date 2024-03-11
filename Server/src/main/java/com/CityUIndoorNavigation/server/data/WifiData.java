package com.CityUIndoorNavigation.server.data;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;



@Entity
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class WifiData {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id = 0;
	
    private String imei;
    private float X = 0;
    private float Y = 0;
    private float ori = 0;
    private int level = 0;
    private String ssid;
    private String bssid;
    private String node_id;
    
    @Override
    public String toString() {
        if (ssid.contains("'")) {
            ssid.replace("\'", "\\'");
            return id + "," + node_id + "," + imei + "," + X + "," + Y + "," + ori + "," +
                    "'" + ssid + "'" + "," + "'" + bssid + "'" + "," + level;
        } else
            return id + "," + node_id + "," + imei + "," + X + "," + Y + "," + ori + "," +
                    "'" + ssid + "'" + "," + "'" + bssid + "'" + "," + level;
    }


}
