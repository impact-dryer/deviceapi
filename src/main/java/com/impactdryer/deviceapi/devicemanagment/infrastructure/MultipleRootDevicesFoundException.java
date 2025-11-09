package com.impactdryer.deviceapi.devicemanagment.infrastructure;

public class MultipleRootDevicesFoundException extends RuntimeException {
    public MultipleRootDevicesFoundException() {
        super("Multiple root devices found in the system");
    }
}
