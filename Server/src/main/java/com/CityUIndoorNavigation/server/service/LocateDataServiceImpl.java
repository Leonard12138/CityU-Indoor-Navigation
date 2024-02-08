package com.CityUIndoorNavigation.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.CityUIndoorNavigation.server.data.WifiData;
import com.CityUIndoorNavigation.server.repository.WifiDataRepository;

@Service
public class LocateDataServiceImpl implements LocateDataService {

    @Autowired
    private WifiDataRepository wifiDataRepository;
    
    @Override
    public void saveWifiData(WifiData wifiData) {
        wifiDataRepository.save(wifiData);
    }
}
