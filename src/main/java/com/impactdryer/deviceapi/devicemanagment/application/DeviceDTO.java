package com.impactdryer.deviceapi.devicemanagment.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDTO {
    private String macAddress;
    private String deviceType;
}
