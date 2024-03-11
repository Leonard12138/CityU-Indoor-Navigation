package com.CityUIndoorNavigation.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.CityUIndoorNavigation.server.data.WifiData;
import com.CityUIndoorNavigation.server.repository.WifiDataRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LocateDataServiceImpl implements LocateDataService {

    @Autowired
    private WifiDataRepository wifiDataRepository;
    
    @Override
    public void saveWifiData(WifiData wifiData) {
    	//log.info("Wifi Data: " + wifiData.toString());
        wifiDataRepository.save(wifiData);
    }

	@Override
	public List<WifiData> getWififingerprint() {
		return wifiDataRepository.findAll();
	}
}
