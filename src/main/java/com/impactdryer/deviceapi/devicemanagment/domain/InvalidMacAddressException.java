package com.impactdryer.deviceapi.devicemanagment.domain;

public class InvalidMacAddressException extends RuntimeException {
  public InvalidMacAddressException(String message) {
    super("Invalid MAC Address: " + message);
  }
}
