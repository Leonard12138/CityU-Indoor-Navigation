package com.CityUIndoorNavigation.server.repository;

import com.CityUIndoorNavigation.server.data.WifiData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WifiDataRepository extends JpaRepository<WifiData, Integer> {
	
    // Insert (Save) method
    <S extends WifiData> S save(S wifiData);

    // Update method
    <S extends WifiData> S saveAndFlush(S wifiData);

    // Delete method
    void deleteById(int id);

    List<WifiData> findBySsid(String ssid);

    List<WifiData> findByBssid(String bssid);

    List<WifiData> findByImeiAndSsid(String imei, String ssid);

    List<WifiData> findByLevelGreaterThan(int level);

    List<WifiData> findBySsidContaining(String substring);
    
    List<WifiData> findByNodeId(String nodeId); 

    @Query(value = "SELECT * FROM wifi_data WHERE level > :level", nativeQuery = true)
    List<WifiData> findCustomByLevel(@Param("level") int level);

    @Query("SELECT w FROM WifiData w WHERE w.level > :level")
    List<WifiData> findCustomByLevelJPQL(@Param("level") int level);
    
    @Query("SELECT DISTINCT w.x, w.y FROM WifiData w WHERE w.nodeId = :nodeId")
    List<Object[]> findDistinctCoordinatesByNodeId(@Param("nodeId") String nodeId);

}
