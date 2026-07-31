package com.sciverse.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sciverse.platform.domain.ProtocolStatus;
import com.sciverse.platform.dto.request.ProtocolCreateRequest;
import com.sciverse.platform.dto.request.ProtocolStepRequest;
import com.sciverse.platform.dto.response.PaginatedResponse;
import com.sciverse.platform.dto.response.ProtocolResponse;
import com.sciverse.platform.dto.response.ProtocolStepResponse;
import com.sciverse.platform.exception.ResourceNotFoundException;
import com.sciverse.platform.service.ProtocolService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProtocolController.class)
class ProtocolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProtocolService protocolService;

    private static final String BASE_URL = "/api/v1/protocols";

    private ProtocolCreateRequest validCreateRequest() {
        ProtocolStepRequest step = new ProtocolStepRequest("Denaturation", new BigDecimal("95.0"), 30);
        ProtocolCreateRequest request = new ProtocolCreateRequest();
        request.setName("Standard PCR");
        request.setDescription("Basic amplification");
        request.setSteps(List.of(step));
        request.setCycleCount(35);
        request.setRampRate(new BigDecimal("4.4"));
        return request;
    }

    private ProtocolResponse sampleResponse(UUID id) {
        ProtocolStepResponse step = new ProtocolStepResponse("Denaturation", new BigDecimal("95.0"), 30);
        return new ProtocolResponse(id, "Standard PCR", "Basic amplification",
                List.of(step), 35, new BigDecimal("4.4"),
                ProtocolStatus.ACTIVE, 0, Instant.now(), Instant.now());
    }

    @Test
    void createProtocol_validRequest_returns201() throws Exception {
        UUID id = UUID.randomUUID();
        when(protocolService.createProtocol(any())).thenReturn(sampleResponse(id));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Standard PCR"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void createProtocol_missingName_returns422() throws Exception {
        ProtocolStepRequest step = new ProtocolStepRequest("Denaturation", new BigDecimal("95.0"), 30);
        ProtocolCreateRequest invalid = new ProtocolCreateRequest();
        invalid.setSteps(List.of(step));
        invalid.setCycleCount(35);
        invalid.setRampRate(new BigDecimal("4.4"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.details[0].field").value("name"));
    }

    @Test
    void createProtocol_cycleCountOutOfRange_returns422() throws Exception {
        ProtocolStepRequest step = new ProtocolStepRequest("Step", new BigDecimal("95.0"), 30);
        ProtocolCreateRequest invalid = new ProtocolCreateRequest();
        invalid.setName("Bad Protocol");
        invalid.setSteps(List.of(step));
        invalid.setCycleCount(999);
        invalid.setRampRate(new BigDecimal("4.4"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.details[0].field").value("cycleCount"));
    }

    @Test
    void getProtocol_existingId_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(protocolService.getProtocol(id)).thenReturn(sampleResponse(id));

        mockMvc.perform(get(BASE_URL + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void getProtocol_unknownId_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(protocolService.getProtocol(id))
                .thenThrow(new ResourceNotFoundException("Protocol", id));

        mockMvc.perform(get(BASE_URL + "/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deleteProtocol_activeProtocol_returns204() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(protocolService).deleteProtocol(id);

        mockMvc.perform(delete(BASE_URL + "/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProtocol_unknownId_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("Protocol", id))
                .when(protocolService).deleteProtocol(id);

        mockMvc.perform(delete(BASE_URL + "/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void listProtocols_defaultParams_returns200WithPaginatedResponse() throws Exception {
        UUID id = UUID.randomUUID();
        PaginatedResponse<ProtocolResponse> pagedResponse = new PaginatedResponse<>(
                List.of(sampleResponse(id)), 0, 20, 1, 1);

        when(protocolService.listProtocols(eq(null), any(Pageable.class))).thenReturn(pagedResponse);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.page").value(0));
    }
}
