package com.sciverse.platform.controller;

import com.sciverse.platform.domain.ProtocolStatus;
import com.sciverse.platform.dto.request.ProtocolCreateRequest;
import com.sciverse.platform.dto.request.ProtocolUpdateRequest;
import com.sciverse.platform.dto.response.PaginatedResponse;
import com.sciverse.platform.dto.response.ProtocolResponse;
import com.sciverse.platform.service.ProtocolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;

@Tag(name = "Protocols", description = "Endpoints for managing PCR thermal cycler protocols and steps")
@RestController
@RequestMapping("/api/v1/protocols")
public class ProtocolController {

    private final ProtocolService protocolService;

    public ProtocolController(ProtocolService protocolService) {
        this.protocolService = protocolService;
    }

    @Operation(summary = "Create a new PCR protocol", description = "Validates and creates a new thermal cycler protocol with execution steps.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProtocolResponse createProtocol(@Valid @RequestBody ProtocolCreateRequest request) {
        return protocolService.createProtocol(request);
    }

    @Operation(summary = "Get protocol by ID", description = "Fetches details of an active PCR protocol by its UUID.")
    @GetMapping("/{id}")
    public ProtocolResponse getProtocol(@PathVariable UUID id) {
        return protocolService.getProtocol(id);
    }

    @Operation(summary = "List protocols", description = "Retrieves a paginated list of protocols with optional status filtering.")
    @GetMapping
    public PaginatedResponse<ProtocolResponse> listProtocols(
            @RequestParam(required = false) ProtocolStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }

        Set<String> allowedSortFields = Set.of("id", "name", "description", "cycleCount", "rampRate", "status", "version", "createdAt", "updatedAt");
        if (!allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sort property: " + sortBy);
        }

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return protocolService.listProtocols(status, pageable);
    }

    @Operation(summary = "Update protocol", description = "Updates an existing active PCR protocol using optimistic concurrency control.")
    @PutMapping("/{id}")
    public ProtocolResponse updateProtocol(
            @PathVariable UUID id,
            @Valid @RequestBody ProtocolUpdateRequest request) {
        return protocolService.updateProtocol(id, request);
    }

    @Operation(summary = "Soft-delete protocol", description = "Marks a protocol status as DELETED without physically removing audit records.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProtocol(@PathVariable UUID id) {
        protocolService.deleteProtocol(id);
    }
}

