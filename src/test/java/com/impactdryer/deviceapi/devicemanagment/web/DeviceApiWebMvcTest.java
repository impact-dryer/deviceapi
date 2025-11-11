package com.impactdryer.deviceapi.devicemanagment.web;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.impactdryer.deviceapi.devicemanagment.infrastructure.AbstractPostgresContainerTest;
import com.impactdryer.deviceapi.infrastructure.openapi.model.DeviceRegistrationRequest;
import com.impactdryer.deviceapi.infrastructure.openapi.model.DeviceType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class DeviceApiWebMvcTest extends AbstractPostgresContainerTest {

    private static final String GATEWAY_MAC = "AA:BB:CC:DD:EE:01";
    private static final String SWITCH_MAC = "AA:BB:CC:DD:EE:02";
    private static final String ACCESS_POINT_MAC = "AA:BB:CC:DD:EE:03";
    private static final String NONEXISTENT_MAC = "FF:FF:FF:FF:FF:FF";
    private static final String INVALID_MAC = "invalid-mac";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /devices - Should register a gateway device without uplink and return 201")
    @Sql(scripts = "/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldRegisterGatewayDeviceWithoutUplink() throws Exception {
        DeviceRegistrationRequest request = new DeviceRegistrationRequest();
        request.setDeviceType(DeviceType.GATEWAY);
        request.setMacAddress(GATEWAY_MAC);

        mockMvc.perform(post("/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "/devices/" + GATEWAY_MAC))
                .andExpect(jsonPath("$.deviceType").value("GATEWAY"))
                .andExpect(jsonPath("$.macAddress").value(GATEWAY_MAC));
    }

    @Test
    @DisplayName("POST /devices - Should register a switch device with uplink and return 201")
    @Sql(scripts = "/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldRegisterSwitchDeviceWithUplink() throws Exception {
        DeviceRegistrationRequest gatewayRequest = new DeviceRegistrationRequest();
        gatewayRequest.setDeviceType(DeviceType.GATEWAY);
        gatewayRequest.setMacAddress(GATEWAY_MAC);

        mockMvc.perform(post("/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gatewayRequest)))
                .andExpect(status().isCreated());

        DeviceRegistrationRequest switchRequest = new DeviceRegistrationRequest();
        switchRequest.setDeviceType(DeviceType.SWITCH);
        switchRequest.setMacAddress(SWITCH_MAC);
        switchRequest.setUplinkMacAddress(GATEWAY_MAC);

        mockMvc.perform(post("/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(switchRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "/devices/" + SWITCH_MAC))
                .andExpect(jsonPath("$.deviceType").value("SWITCH"))
                .andExpect(jsonPath("$.macAddress").value(SWITCH_MAC));
    }

    @Test
    @Order(3)
    @DisplayName("POST /devices - Should return 400 for invalid MAC address format")
    void shouldReturnBadRequestForInvalidMacAddress() throws Exception {
        DeviceRegistrationRequest request = new DeviceRegistrationRequest();
        request.setDeviceType(DeviceType.GATEWAY);
        request.setMacAddress(INVALID_MAC);

        mockMvc.perform(post("/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("POST /devices - Should return 409 when registering duplicate MAC address")
    @Sql(scripts = "/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnConflictForDuplicateMacAddress() throws Exception {
        DeviceRegistrationRequest request = new DeviceRegistrationRequest();
        request.setDeviceType(DeviceType.GATEWAY);
        request.setMacAddress(GATEWAY_MAC);

        mockMvc.perform(post("/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("POST /devices - Should return 404 when uplink MAC address does not exist")
    @Sql(scripts = "/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnNotFoundForNonexistentUplink() throws Exception {
        DeviceRegistrationRequest request = new DeviceRegistrationRequest();
        request.setDeviceType(DeviceType.SWITCH);
        request.setMacAddress(SWITCH_MAC);
        request.setUplinkMacAddress(NONEXISTENT_MAC);

        mockMvc.perform(post("/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("POST /devices - Should return 400 for missing required fields")
    void shouldReturnBadRequestForMissingRequiredFields() throws Exception {
        mockMvc.perform(post("/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"macAddress\":\"" + GATEWAY_MAC + "\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /devices - Should return empty list when no devices exist")
    @Sql(scripts = "/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnEmptyListWhenNoDevicesExist() throws Exception {
        mockMvc.perform(get("/devices").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("GET /devices - Should return all devices sorted by type (GATEWAY > SWITCH > ACCESS_POINT)")
    @Sql(scripts = "/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnDevicesSortedByType() throws Exception {
        registerDevice(DeviceType.ACCESS_POINT, ACCESS_POINT_MAC, null);
        registerDevice(DeviceType.SWITCH, SWITCH_MAC, null);
        registerDevice(DeviceType.GATEWAY, GATEWAY_MAC, null);

        mockMvc.perform(get("/devices").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].deviceType").value("GATEWAY"))
                .andExpect(jsonPath("$[0].macAddress").value(GATEWAY_MAC))
                .andExpect(jsonPath("$[1].deviceType").value("SWITCH"))
                .andExpect(jsonPath("$[1].macAddress").value(SWITCH_MAC))
                .andExpect(jsonPath("$[2].deviceType").value("ACCESS_POINT"))
                .andExpect(jsonPath("$[2].macAddress").value(ACCESS_POINT_MAC));
    }

    @Test
    @DisplayName("GET /devices - Should return devices with only deviceType and macAddress fields")
    @Sql(scripts = "/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnDevicesWithOnlyRequiredFields() throws Exception {
        registerDevice(DeviceType.GATEWAY, GATEWAY_MAC, null);

        MvcResult result = mockMvc.perform(get("/devices").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].deviceType").exists())
                .andExpect(jsonPath("$[0].macAddress").exists())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        org.assertj.core.api.Assertions.assertThat(response).doesNotContain("uplinkMacAddress");
    }

    @Test
    @DisplayName("GET /devices/{macAddress} - Should return device when found")
    @Sql(scripts = "/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnDeviceWhenFound() throws Exception {
        registerDevice(DeviceType.GATEWAY, GATEWAY_MAC, null);

        mockMvc.perform(get("/devices/{macAddress}", GATEWAY_MAC).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deviceType").value("GATEWAY"))
                .andExpect(jsonPath("$.macAddress").value(GATEWAY_MAC));
    }

    @Test
    @DisplayName("GET /devices/{macAddress} - Should return 404 when device not found")
    @Sql(scripts = "/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnNotFoundWhenDeviceDoesNotExist() throws Exception {
        mockMvc.perform(get("/devices/{macAddress}", NONEXISTENT_MAC).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").exists());
    }

    @Test
    @DisplayName("GET /devices/{macAddress} - Should handle MAC address case-insensitively")
    @Sql(scripts = "/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldHandleMacAddressCaseInsensitively() throws Exception {
        registerDevice(DeviceType.GATEWAY, GATEWAY_MAC.toUpperCase(), null);

        mockMvc.perform(get("/devices/{macAddress}", GATEWAY_MAC.toLowerCase()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.macAddress").value(GATEWAY_MAC.toUpperCase()));
    }

    @Test
    @DisplayName("GET /topology - Should return 404 when no devices exist")
    @Sql(scripts = "/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnEmptyTopologyWhenNoDevicesExist() throws Exception {
        mockMvc.perform(get("/topology").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    @DisplayName("GET /topology - Should return single root device topology")
    @Sql(scripts = "/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnSingleRootDeviceTopology() throws Exception {
        registerDevice(DeviceType.GATEWAY, GATEWAY_MAC, null);

        mockMvc.perform(get("/topology").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].macAddress").value(GATEWAY_MAC))
                .andExpect(jsonPath("$[0].children").isArray())
                .andExpect(jsonPath("$[0].children").isEmpty());
    }

    @Test
    @Order(32)
    @Sql(scripts = "/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnHierarchicalTopology() throws Exception {
        registerDevice(DeviceType.GATEWAY, GATEWAY_MAC, null);
        registerDevice(DeviceType.SWITCH, SWITCH_MAC, GATEWAY_MAC);
        registerDevice(DeviceType.ACCESS_POINT, ACCESS_POINT_MAC, SWITCH_MAC);

        mockMvc.perform(get("/topology").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].macAddress").value(GATEWAY_MAC))
                .andExpect(jsonPath("$[0].children", hasSize(1)))
                .andExpect(jsonPath("$[0].children[0].macAddress").value(SWITCH_MAC))
                .andExpect(jsonPath("$[0].children[0].children", hasSize(1)))
                .andExpect(jsonPath("$[0].children[0].children[0].macAddress").value(ACCESS_POINT_MAC))
                .andExpect(jsonPath("$[0].children[0].children[0].children").isEmpty());
    }

    @Test
    @DisplayName("GET /topology - Should return bad request when multiple root devices exist")
    @Sql(scripts = "/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnMultipleRootDevicesTopology() throws Exception {
        String gateway1 = "AA:BB:CC:DD:EE:01";
        String gateway2 = "BB:BB:CC:DD:EE:02";

        registerDevice(DeviceType.GATEWAY, gateway1, null);
        registerDevice(DeviceType.GATEWAY, gateway2, null);

        mockMvc.perform(get("/topology").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    @DisplayName("GET /topology - Should return topology with multiple children per parent")
    @Sql(scripts = "/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnTopologyWithMultipleChildren() throws Exception {
        String switch1 = "BB:BB:CC:DD:EE:02";
        String switch2 = "CC:BB:CC:DD:EE:03";

        registerDevice(DeviceType.GATEWAY, GATEWAY_MAC, null);
        registerDevice(DeviceType.SWITCH, switch1, GATEWAY_MAC);
        registerDevice(DeviceType.SWITCH, switch2, GATEWAY_MAC);

        mockMvc.perform(get("/topology").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].macAddress").value(GATEWAY_MAC))
                .andExpect(jsonPath("$[0].children", hasSize(2)))
                .andExpect(jsonPath("$[0].children[*].macAddress", containsInAnyOrder(switch1, switch2)));
    }

    @Test
    @DisplayName("GET /topology/{macAddress} - Should return subtree from specified device")
    @Sql(scripts = "/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnSubtreeFromSpecifiedDevice() throws Exception {
        registerDevice(DeviceType.GATEWAY, GATEWAY_MAC, null);
        registerDevice(DeviceType.SWITCH, SWITCH_MAC, GATEWAY_MAC);
        registerDevice(DeviceType.ACCESS_POINT, ACCESS_POINT_MAC, SWITCH_MAC);

        mockMvc.perform(get("/topology/{macAddress}", SWITCH_MAC).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.macAddress").value(SWITCH_MAC))
                .andExpect(jsonPath("$.children", hasSize(1)))
                .andExpect(jsonPath("$.children[0].macAddress").value(ACCESS_POINT_MAC))
                .andExpect(jsonPath("$.children[0].children").isEmpty());
    }

    @Test
    @DisplayName("GET /topology/{macAddress} - Should return leaf node with empty children")
    @Sql(scripts = "/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnLeafNodeWithEmptyChildren() throws Exception {
        registerDevice(DeviceType.GATEWAY, GATEWAY_MAC, null);
        registerDevice(DeviceType.SWITCH, SWITCH_MAC, GATEWAY_MAC);

        mockMvc.perform(get("/topology/{macAddress}", SWITCH_MAC).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.macAddress").value(SWITCH_MAC))
                .andExpect(jsonPath("$.children").isArray())
                .andExpect(jsonPath("$.children").isEmpty());
    }

    @Test
    @DisplayName("GET /topology/{macAddress} - Should return 404 when device not found")
    @Sql(scripts = "/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnNotFoundForSubtreeOfNonexistentDevice() throws Exception {
        mockMvc.perform(get("/topology/{macAddress}", NONEXISTENT_MAC).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").exists());
    }

    @Test
    @DisplayName("GET /topology/{macAddress} - Should return full tree when querying root device")
    @Sql(scripts = "/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnFullTreeWhenQueryingRootDevice() throws Exception {
        registerDevice(DeviceType.GATEWAY, GATEWAY_MAC, null);
        registerDevice(DeviceType.SWITCH, SWITCH_MAC, GATEWAY_MAC);
        registerDevice(DeviceType.ACCESS_POINT, ACCESS_POINT_MAC, SWITCH_MAC);

        mockMvc.perform(get("/topology/{macAddress}", GATEWAY_MAC).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.macAddress").value(GATEWAY_MAC))
                .andExpect(jsonPath("$.children", hasSize(1)))
                .andExpect(jsonPath("$.children[0].macAddress").value(SWITCH_MAC))
                .andExpect(jsonPath("$.children[0].children", hasSize(1)))
                .andExpect(jsonPath("$.children[0].children[0].macAddress").value(ACCESS_POINT_MAC));
    }

    @Test
    @Order(50)
    @DisplayName("Should handle MAC address normalization (uppercase/lowercase)")
    @Sql(scripts = "/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldHandleMacAddressNormalization() throws Exception {
        String mixedCaseMac = "aA:bB:cC:dD:eE:01";

        DeviceRegistrationRequest request = new DeviceRegistrationRequest();
        request.setDeviceType(DeviceType.GATEWAY);
        request.setMacAddress(mixedCaseMac);

        mockMvc.perform(post("/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.macAddress").value(mixedCaseMac.toUpperCase()));
    }

    @Test
    @DisplayName("Should handle all device types in same request")
    @Sql(scripts = "/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldHandleAllDeviceTypes() throws Exception {
        registerDevice(DeviceType.GATEWAY, "AA:BB:CC:DD:EE:01", null);
        registerDevice(DeviceType.SWITCH, "AA:BB:CC:DD:EE:02", null);
        registerDevice(DeviceType.ACCESS_POINT, "AA:BB:CC:DD:EE:03", null);

        mockMvc.perform(get("/devices").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].deviceType").value("GATEWAY"))
                .andExpect(jsonPath("$[1].deviceType").value("SWITCH"))
                .andExpect(jsonPath("$[2].deviceType").value("ACCESS_POINT"));
    }

    private void registerDevice(DeviceType deviceType, String macAddress, String uplinkMacAddress) throws Exception {
        DeviceRegistrationRequest request = new DeviceRegistrationRequest();
        request.setDeviceType(deviceType);
        request.setMacAddress(macAddress);
        request.setUplinkMacAddress(uplinkMacAddress);

        mockMvc.perform(post("/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
