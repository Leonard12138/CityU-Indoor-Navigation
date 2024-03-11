package com.CityUIndoorNavigation.server.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.CityUIndoorNavigation.server.data.WifiData;

@Service
public interface indoorLocateService {

	String locateUser(List<WifiData> wifiDataList);



}
