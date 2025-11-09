package com.impactdryer.deviceapi.devicemanagment.infrastructure;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<DeviceEntity, Long> {
    Optional<DeviceEntity> findByMacAddress(String macAddress);

    @Query(
            value =
                    "SELECT * FROM devices ORDER BY CASE device_type WHEN 'GATEWAY' THEN 0 WHEN 'SWITCH' THEN 1 WHEN 'ACCESS_POINT' THEN 2 ELSE 99 END, mac_address",
            nativeQuery = true)
    List<DeviceEntity> findAllSortedByType();
}
