package com.sciverse.platform.service;

import com.sciverse.platform.domain.ProtocolStatus;
import com.sciverse.platform.dto.request.ProtocolCreateRequest;
import com.sciverse.platform.dto.request.ProtocolUpdateRequest;
import com.sciverse.platform.dto.response.PaginatedResponse;
import com.sciverse.platform.dto.response.ProtocolResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProtocolService {

    ProtocolResponse createProtocol(ProtocolCreateRequest request);

    ProtocolResponse getProtocol(UUID id);

    PaginatedResponse<ProtocolResponse> listProtocols(ProtocolStatus status, Pageable pageable);

    ProtocolResponse updateProtocol(UUID id, ProtocolUpdateRequest request);

    void deleteProtocol(UUID id);
}
