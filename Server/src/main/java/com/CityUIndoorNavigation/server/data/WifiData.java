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
	private int x = 0;
	private int y = 0;
	private float ori = 0;
	private int level = 0;
	private String ssid;
	private String bssid;
	private String nodeId;

	@Override
	public String toString() {
		if (ssid.contains("'")) {
			ssid.replace("\'", "\\'");
			return id + "," + nodeId + "," + imei + "," + x + "," + y + "," + ori + "," + "'" + ssid + "'" + "," + "'"
					+ bssid + "'" + "," + level;
		} else
			return id + "," + nodeId + "," + imei + "," + x + "," + y + "," + ori + "," + "'" + ssid + "'" + "," + "'"
					+ bssid + "'" + "," + level;
	}

}
