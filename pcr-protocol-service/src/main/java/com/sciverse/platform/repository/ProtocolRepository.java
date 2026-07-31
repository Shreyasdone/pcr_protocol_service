package com.sciverse.platform.repository;

import com.sciverse.platform.domain.Protocol;
import com.sciverse.platform.domain.ProtocolStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProtocolRepository extends JpaRepository<Protocol, UUID> {

    Page<Protocol> findByStatus(ProtocolStatus status, Pageable pageable);

    Optional<Protocol> findByIdAndStatusNot(UUID id, ProtocolStatus status);
}
