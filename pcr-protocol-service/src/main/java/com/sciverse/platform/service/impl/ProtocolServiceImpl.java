package com.sciverse.platform.service.impl;

import com.sciverse.platform.domain.Protocol;
import com.sciverse.platform.domain.ProtocolStatus;
import com.sciverse.platform.domain.ProtocolStep;
import com.sciverse.platform.dto.request.ProtocolCreateRequest;
import com.sciverse.platform.dto.request.ProtocolStepRequest;
import com.sciverse.platform.dto.request.ProtocolUpdateRequest;
import com.sciverse.platform.dto.response.PaginatedResponse;
import com.sciverse.platform.dto.response.ProtocolResponse;
import com.sciverse.platform.dto.response.ProtocolStepResponse;
import com.sciverse.platform.exception.ResourceNotFoundException;
import com.sciverse.platform.repository.ProtocolRepository;
import com.sciverse.platform.service.ProtocolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProtocolServiceImpl implements ProtocolService {

    private static final Logger log = LoggerFactory.getLogger(ProtocolServiceImpl.class);

    private final ProtocolRepository protocolRepository;

    public ProtocolServiceImpl(ProtocolRepository protocolRepository) {
        this.protocolRepository = protocolRepository;
    }

    @Override
    @Transactional
    public ProtocolResponse createProtocol(ProtocolCreateRequest request) {
        Protocol protocol = new Protocol();
        protocol.setName(request.getName());
        protocol.setDescription(request.getDescription());
        protocol.setSteps(mapSteps(request.getSteps()));
        protocol.setCycleCount(request.getCycleCount());
        protocol.setRampRate(request.getRampRate());
        protocol.setStatus(request.getStatus() != null ? request.getStatus() : ProtocolStatus.ACTIVE);

        Protocol saved = protocolRepository.save(protocol);
        log.info("Created protocol id={} name={}", saved.getId(), saved.getName());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProtocolResponse getProtocol(UUID id) {
        Protocol protocol = protocolRepository.findByIdAndStatusNot(id, ProtocolStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Protocol", id));
        return toResponse(protocol);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ProtocolResponse> listProtocols(ProtocolStatus status, Pageable pageable) {
        ProtocolStatus effectiveStatus = status != null ? status : ProtocolStatus.ACTIVE;
        Page<Protocol> page = protocolRepository.findByStatus(effectiveStatus, pageable);

        List<ProtocolResponse> content = page.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new PaginatedResponse<>(content, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }

    @Override
    @Transactional
    public ProtocolResponse updateProtocol(UUID id, ProtocolUpdateRequest request) {
        if (request.getName() == null && request.getDescription() == null && request.getSteps() == null &&
                request.getCycleCount() == null && request.getRampRate() == null && request.getStatus() == null) {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }

        Protocol protocol = protocolRepository.findByIdAndStatusNot(id, ProtocolStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Protocol", id));

        if (request.getVersion() != null && !request.getVersion().equals(protocol.getVersion())) {
            throw new org.springframework.orm.ObjectOptimisticLockingFailureException(Protocol.class, id);
        }

        if (request.getName() != null) protocol.setName(request.getName());
        if (request.getDescription() != null) protocol.setDescription(request.getDescription());
        if (request.getSteps() != null) protocol.setSteps(mapSteps(request.getSteps()));
        if (request.getCycleCount() != null) protocol.setCycleCount(request.getCycleCount());
        if (request.getRampRate() != null) protocol.setRampRate(request.getRampRate());
        if (request.getStatus() != null) protocol.setStatus(request.getStatus());

        Protocol updated = protocolRepository.save(protocol);
        log.info("Updated protocol id={}", updated.getId());
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteProtocol(UUID id) {
        Protocol protocol = protocolRepository.findByIdAndStatusNot(id, ProtocolStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Protocol", id));

        protocol.setStatus(ProtocolStatus.DELETED);
        protocolRepository.save(protocol);
        log.info("Soft-deleted protocol id={}", id);
    }

    private List<ProtocolStep> mapSteps(List<ProtocolStepRequest> stepRequests) {
        return new java.util.ArrayList<>(stepRequests.stream()
                .map(s -> new ProtocolStep(s.getName(), s.getTargetTemperatureCelsius(), s.getHoldTimeSeconds()))
                .toList());
    }

    private ProtocolResponse toResponse(Protocol protocol) {
        List<ProtocolStepResponse> stepResponses = protocol.getSteps().stream()
                .map(s -> new ProtocolStepResponse(s.getName(), s.getTargetTemperatureCelsius(), s.getHoldTimeSeconds()))
                .toList();

        return new ProtocolResponse(
                protocol.getId(),
                protocol.getName(),
                protocol.getDescription(),
                stepResponses,
                protocol.getCycleCount(),
                protocol.getRampRate(),
                protocol.getStatus(),
                protocol.getVersion(),
                protocol.getCreatedAt(),
                protocol.getUpdatedAt()
        );
    }
}
