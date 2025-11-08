package com.impactdryer.deviceapi.devicemanagment.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MacAddressTest {
  @Test
  void testValidMacAddress() {
    String validMac = "00:1A:2B:3C:4D:5E";
    MacAddress macAddress = new MacAddress(validMac);
    assertEquals(validMac, macAddress.value());
  }

  @Test
  void testInvalidMacAddress() {
    String invalidMac = "00:1A:2B:3C:4D:5Z"; // Invalid character 'Z'
    assertThrows(InvalidMacAddressException.class, () -> new MacAddress(invalidMac));
  }

  @Test
  void testNullMacAddress() {
    assertThrows(InvalidMacAddressException.class, () -> new MacAddress(null));
  }

  @Test
  void testEmptyMacAddress() {
    assertThrows(InvalidMacAddressException.class, () -> new MacAddress(""));
  }
}
