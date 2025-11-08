package com.impactdryer.deviceapi.devicemanagment.application;

import java.util.UUID;

public interface RegisterDeviceHandler {
  UUID registerDevice(RegisterDeviceCommand command);
}
