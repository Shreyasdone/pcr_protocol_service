package com.sciverse.platform.config;

import com.sciverse.platform.domain.Protocol;
import com.sciverse.platform.domain.ProtocolStatus;
import com.sciverse.platform.domain.ProtocolStep;
import com.sciverse.platform.repository.ProtocolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final ProtocolRepository protocolRepository;

    public DataSeeder(ProtocolRepository protocolRepository) {
        this.protocolRepository = protocolRepository;
    }

    @Override
    public void run(String... args) {
        if (protocolRepository.count() > 0) {
            log.info("Database already contains protocols; skipping data seeding.");
            return;
        }

        log.info("Seeding sample PCR protocol test data...");

        // 1. Standard 3-Step PCR Protocol
        Protocol standardPcr = new Protocol();
        standardPcr.setName("Standard 3-Step PCR");
        standardPcr.setDescription("Standard 3-step amplification protocol for general DNA analysis and cloning.");
        standardPcr.setCycleCount(30);
        standardPcr.setRampRate(new BigDecimal("2.5"));
        standardPcr.setStatus(ProtocolStatus.ACTIVE);
        standardPcr.setSteps(List.of(
                new ProtocolStep("Initial Denaturation", new BigDecimal("95.0"), 180),
                new ProtocolStep("Denaturation", new BigDecimal("95.0"), 30),
                new ProtocolStep("Annealing", new BigDecimal("55.0"), 30),
                new ProtocolStep("Extension", new BigDecimal("72.0"), 60),
                new ProtocolStep("Final Extension", new BigDecimal("72.0"), 300)
        ));

        // 2. Fast 2-Step PCR Protocol
        Protocol fastPcr = new Protocol();
        fastPcr.setName("Fast 2-Step High-Yield PCR");
        fastPcr.setDescription("Rapid 2-step protocol optimized for fast DNA polymerases.");
        fastPcr.setCycleCount(35);
        fastPcr.setRampRate(new BigDecimal("4.0"));
        fastPcr.setStatus(ProtocolStatus.ACTIVE);
        fastPcr.setSteps(List.of(
                new ProtocolStep("Denaturation", new BigDecimal("98.0"), 10),
                new ProtocolStep("Anneal & Extension", new BigDecimal("60.0"), 20)
        ));

        // 3. High-Fidelity Long-Range PCR
        Protocol longRangePcr = new Protocol();
        longRangePcr.setName("High-Fidelity Long-Range PCR");
        longRangePcr.setDescription("Optimized protocol for long fragment genomic DNA amplification.");
        longRangePcr.setCycleCount(28);
        longRangePcr.setRampRate(new BigDecimal("2.0"));
        longRangePcr.setStatus(ProtocolStatus.ACTIVE);
        longRangePcr.setSteps(List.of(
                new ProtocolStep("Initial Denaturation", new BigDecimal("98.0"), 120),
                new ProtocolStep("Denaturation", new BigDecimal("98.0"), 15),
                new ProtocolStep("Annealing", new BigDecimal("62.0"), 30),
                new ProtocolStep("Extension", new BigDecimal("72.0"), 180),
                new ProtocolStep("Final Extension", new BigDecimal("72.0"), 600)
        ));

        // 4. Soft-deleted Legacy Protocol (for testing status filters)
        Protocol legacyPcr = new Protocol();
        legacyPcr.setName("Legacy Taq Assay (Archived)");
        legacyPcr.setDescription("Deprecated end-point PCR protocol.");
        legacyPcr.setCycleCount(40);
        legacyPcr.setRampRate(new BigDecimal("1.5"));
        legacyPcr.setStatus(ProtocolStatus.DELETED);
        legacyPcr.setSteps(List.of(
                new ProtocolStep("Denaturation", new BigDecimal("94.0"), 45),
                new ProtocolStep("Annealing", new BigDecimal("52.0"), 45),
                new ProtocolStep("Extension", new BigDecimal("72.0"), 90)
        ));

        protocolRepository.saveAll(List.of(standardPcr, fastPcr, longRangePcr, legacyPcr));
        log.info("Successfully seeded 4 sample PCR protocols.");
    }
}
