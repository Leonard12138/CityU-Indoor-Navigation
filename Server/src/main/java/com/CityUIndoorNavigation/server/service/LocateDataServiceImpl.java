package com.CityUIndoorNavigation.server.service;


import org.springframework.stereotype.Service;

import com.CityUIndoorNavigation.server.data.WifiData;
import com.CityUIndoorNavigation.server.util.database.DatabaseOperation;

@Service
public class LocateDataServiceImpl implements LocateDataService{
	private String wifiTableName = "wifi_data";
	
	private DatabaseOperation mDatabaseOperation;

	@Override
	public void saveWifiData(WifiData wifiData) {
		int id;
		String driverName = "org.postgresql.Driver";    // 驱动名
        String url = "jdbc:postgresql://127.0.0.1:5432/indoor_location_data";
        String user = "postgres";
        String password = "c724797";
        mDatabaseOperation = new DatabaseOperation(driverName, url, user, password);

		id = mDatabaseOperation.checkTableLastID(wifiTableName);
        id++;
        wifiData.setId(id);
        mDatabaseOperation.updateTable(wifiTableName, wifiData);
	}}
