package com.sciverse.platform.service;

import com.sciverse.platform.domain.Protocol;
import com.sciverse.platform.domain.ProtocolStatus;
import com.sciverse.platform.domain.ProtocolStep;
import com.sciverse.platform.dto.request.ProtocolCreateRequest;
import com.sciverse.platform.dto.request.ProtocolStepRequest;
import com.sciverse.platform.dto.request.ProtocolUpdateRequest;
import com.sciverse.platform.dto.response.PaginatedResponse;
import com.sciverse.platform.dto.response.ProtocolResponse;
import com.sciverse.platform.exception.ResourceNotFoundException;
import com.sciverse.platform.repository.ProtocolRepository;
import com.sciverse.platform.service.impl.ProtocolServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProtocolServiceImplTest {

    @Mock
    private ProtocolRepository protocolRepository;

    @InjectMocks
    private ProtocolServiceImpl protocolService;

    private Protocol activeProtocol;
    private UUID protocolId;

    @BeforeEach
    void setUp() {
        protocolId = UUID.randomUUID();

        ProtocolStep step = new ProtocolStep("Denaturation", new BigDecimal("95.0"), 30);

        activeProtocol = new Protocol();
        activeProtocol.setId(protocolId);
        activeProtocol.setName("Standard PCR");
        activeProtocol.setDescription("Basic amplification protocol");
        activeProtocol.setSteps(List.of(step));
        activeProtocol.setCycleCount(35);
        activeProtocol.setRampRate(new BigDecimal("4.4"));
        activeProtocol.setStatus(ProtocolStatus.ACTIVE);
        activeProtocol.setVersion(0);
        activeProtocol.setCreatedAt(Instant.now());
        activeProtocol.setUpdatedAt(Instant.now());
    }

    @Test
    void createProtocol_validRequest_returnsCreatedProtocol() {
        ProtocolStepRequest stepRequest = new ProtocolStepRequest("Denaturation", new BigDecimal("95.0"), 30);
        ProtocolCreateRequest request = new ProtocolCreateRequest();
        request.setName("Standard PCR");
        request.setDescription("Basic amplification protocol");
        request.setSteps(List.of(stepRequest));
        request.setCycleCount(35);
        request.setRampRate(new BigDecimal("4.4"));

        when(protocolRepository.save(any(Protocol.class))).thenReturn(activeProtocol);

        ProtocolResponse response = protocolService.createProtocol(request);

        assertThat(response.getName()).isEqualTo("Standard PCR");
        assertThat(response.getStatus()).isEqualTo(ProtocolStatus.ACTIVE);
        verify(protocolRepository).save(any(Protocol.class));
    }

    @Test
    void getProtocol_existingActiveProtocol_returnsProtocol() {
        when(protocolRepository.findByIdAndStatusNot(protocolId, ProtocolStatus.DELETED))
                .thenReturn(Optional.of(activeProtocol));

        ProtocolResponse response = protocolService.getProtocol(protocolId);

        assertThat(response.getId()).isEqualTo(protocolId);
        assertThat(response.getName()).isEqualTo("Standard PCR");
    }

    @Test
    void getProtocol_notFound_throwsResourceNotFoundException() {
        when(protocolRepository.findByIdAndStatusNot(protocolId, ProtocolStatus.DELETED))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> protocolService.getProtocol(protocolId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(protocolId.toString());
    }

    @Test
    void deleteProtocol_activeProtocol_setsStatusToDeleted() {
        when(protocolRepository.findByIdAndStatusNot(protocolId, ProtocolStatus.DELETED))
                .thenReturn(Optional.of(activeProtocol));
        when(protocolRepository.save(any(Protocol.class))).thenReturn(activeProtocol);

        protocolService.deleteProtocol(protocolId);

        assertThat(activeProtocol.getStatus()).isEqualTo(ProtocolStatus.DELETED);
        verify(protocolRepository).save(activeProtocol);
    }

    @Test
    void deleteProtocol_alreadyDeleted_throwsResourceNotFoundException() {
        when(protocolRepository.findByIdAndStatusNot(protocolId, ProtocolStatus.DELETED))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> protocolService.deleteProtocol(protocolId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void listProtocols_returnsPagedResults() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Protocol> protocolPage = new PageImpl<>(List.of(activeProtocol), pageable, 1);

        when(protocolRepository.findByStatus(ProtocolStatus.ACTIVE, pageable))
                .thenReturn(protocolPage);

        PaginatedResponse<ProtocolResponse> response =
                protocolService.listProtocols(ProtocolStatus.ACTIVE, pageable);

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getPage()).isEqualTo(0);
        assertThat(response.getSize()).isEqualTo(20);
    }

    @Test
    void updateProtocol_validRequest_updatesFields() {
        ProtocolStepRequest stepRequest = new ProtocolStepRequest("Denaturation", new BigDecimal("94.0"), 45);
        ProtocolUpdateRequest request = new ProtocolUpdateRequest();
        request.setName("Updated PCR");
        request.setSteps(List.of(stepRequest));
        request.setCycleCount(40);
        request.setRampRate(new BigDecimal("3.0"));
        request.setVersion(0);
        request.setStatus(ProtocolStatus.ARCHIVED);

        when(protocolRepository.findByIdAndStatusNot(protocolId, ProtocolStatus.DELETED))
                .thenReturn(Optional.of(activeProtocol));
        when(protocolRepository.save(any(Protocol.class))).thenReturn(activeProtocol);

        protocolService.updateProtocol(protocolId, request);

        assertThat(activeProtocol.getName()).isEqualTo("Updated PCR");
        assertThat(activeProtocol.getCycleCount()).isEqualTo(40);
        assertThat(activeProtocol.getStatus()).isEqualTo(ProtocolStatus.ARCHIVED);
        verify(protocolRepository).save(activeProtocol);
    }
}
