package com.impactdryer.deviceapi.devicemanagment.infrastructure;

public class NoRootDeviceFoundException extends RuntimeException {
    public NoRootDeviceFoundException() {
        super("No root device found in the system");
    }
}
