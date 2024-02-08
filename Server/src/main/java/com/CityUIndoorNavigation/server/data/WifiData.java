package com.CityUIndoorNavigation.server.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class WifiData {
    private int id = 0;
    private String imei;
    private float X = 0;
    private float Y = 0;
    private float ori = 0;
    private int level = 0;
    private String ssid;
    private String bssid;
    
    @Override
    public String toString() {
        if (ssid.contains("'")) {
            ssid.replace("\'", "\\'");
            return id + "," + imei + "," + X + "," + Y + "," + ori + "," +
                    "'" + ssid + "'" + "," + "'" + bssid + "'" + "," + level;
        } else
            return id + "," + imei + "," + X + "," + Y + "," + ori + "," +
                    "'" + ssid + "'" + "," + "'" + bssid + "'" + "," + level;
    }


}
