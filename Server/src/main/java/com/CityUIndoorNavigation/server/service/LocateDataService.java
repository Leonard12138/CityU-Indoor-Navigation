package com.CityUIndoorNavigation.server.service;


import java.util.List;

import com.CityUIndoorNavigation.server.data.WifiData;

public interface LocateDataService {

    void saveWifiData(WifiData wifiData);

	List<WifiData> getWifiData();
}