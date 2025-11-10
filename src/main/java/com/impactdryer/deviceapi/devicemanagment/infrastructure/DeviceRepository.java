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
            WITH RECURSIVE devices_tree AS (
                SELECT
                    id,
                    device_type,
                    mac_address,
                    uplink_id,
                    version,
                    ARRAY[id] as path  -- Track the path to detect duplicates
                FROM devices
                WHERE uplink_id IS NULL

                UNION ALL

                SELECT
                    d.id,
                    d.device_type,
                    d.mac_address,
                    d.uplink_id,
                    d.version,
                    dt.path || d.id
                FROM devices d
                         INNER JOIN devices_tree dt ON d.uplink_id = dt.id
                WHERE NOT d.id = ANY(dt.path)
            )
            SELECT
                id,
                device_type,
                mac_address,
                uplink_id,
                version
            FROM devices_tree
            ORDER BY path;
            """, nativeQuery = true)
    List<DeviceEntity> findAllRecursive();

    @Query(value = """

                    WITH RECURSIVE devices_tree AS (
                SELECT
                    id,
                    device_type,
                    mac_address,
                    uplink_id,
                    version,
                    ARRAY[id] as path  -- Track the path to detect duplicates
                FROM devices
                WHERE mac_address = :macAddress

                UNION ALL

                SELECT
                    d.id,
                    d.device_type,
                    d.mac_address,
                    d.uplink_id,
                    d.version,
                    dt.path || d.id
                FROM devices d
                         INNER JOIN devices_tree dt ON d.uplink_id = dt.id
                WHERE NOT d.id = ANY(dt.path)
            )
            SELECT
                id,
                device_type,
                mac_address,
                uplink_id,
                version
            FROM devices_tree
            ORDER BY path;
            """, nativeQuery = true)
    List<DeviceEntity> findAllFromDevice(String macAddress);
}
