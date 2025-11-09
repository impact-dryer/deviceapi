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

    @Query(value = """
            WITH RECURSIVE devices_tree (
                                         id,
                                         device_type,
                                         mac_address,
                                         uplink_id

                ) AS (
                SELECT
                    id,
                    device_type,
                    mac_address,
                    uplink_id,
                    version
                FROM devices
                WHERE uplink_id IS NOT NULL
                UNION
                SELECT
                    d.id,
                    d.device_type,
                    d.mac_address,
                    d.uplink_id,
                    d.version
                FROM devices d
                         INNER JOIN devices_tree sub ON sub.uplink_id = d.id
            )
            SELECT *
            FROM devices_tree order by uplink_id desc;
            """, nativeQuery = true)
    List<DeviceEntity> findAllRecursive();
}
