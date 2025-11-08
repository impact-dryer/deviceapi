package com.impactdryer.deviceapi.devicemanagment.infrastructure;

import com.impactdryer.deviceapi.devicemanagment.domain.MacAddress;

import java.util.Random;

public class TestingUtils {

  public static MacAddress getRandomMacAddress() {
    return new MacAddress(randomMACAddress());
  }

  // https://stackoverflow.com/a/24262057
  private static String randomMACAddress() {
    Random rand = new Random();
    byte[] macAddr = new byte[6];
    rand.nextBytes(macAddr);
    macAddr[0] = (byte) (macAddr[0] & (byte) 254);
    StringBuilder sb = new StringBuilder(18);
    for (byte b : macAddr) {
      if (!sb.isEmpty())
        sb.append(":");
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }
}
