package com.impactdryer.deviceapi.devicemanagment.infrastructure;

import com.impactdryer.deviceapi.devicemanagment.domain.DeviceType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;


import static com.impactdryer.deviceapi.devicemanagment.infrastructure.TestingUtils.getRandomMacAddress;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DeviceTreeRepositoryIntegrationTest extends AbstractPostgresContainerTest {

  @Autowired
  private DeviceTreeRepository deviceTreeRepository;

  private static @NotNull DeviceNode getRandomDeviceNode() {
    return new DeviceNode(getRandomMacAddress(), DeviceType.SWITCH);
  }

  @BeforeEach
  void setup() {
    deviceTreeRepository.deleteAll();
  }

  @Test
  void testSaveAndFindDeviceTree() {
    DeviceTreeEntity deviceTreeEntity = new DeviceTreeEntity();

    DeviceNode root = getRandomDeviceNode();
    root.addDownlink(getRandomDeviceNode());
    deviceTreeEntity.setTree(root);

    DeviceTreeEntity save = deviceTreeRepository.save(deviceTreeEntity);

    DeviceTreeEntity found = deviceTreeRepository.findById(save.getId()).orElseThrow();
    Assertions.assertEquals(found.getTree(), root);
  }
}
