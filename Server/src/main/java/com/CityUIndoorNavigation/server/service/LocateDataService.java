package com.CityUIndoorNavigation.server.service;


import java.util.List;

import org.springframework.stereotype.Service;

import com.CityUIndoorNavigation.server.data.WifiData;

@Service
public interface LocateDataService {

    void saveWifiData(WifiData wifiData);

	List<WifiData> getWififingerprint();
}