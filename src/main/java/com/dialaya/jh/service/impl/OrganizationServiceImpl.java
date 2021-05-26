package com.dialaya.jh.service.impl;

import com.dialaya.jh.domain.Organization;
import com.dialaya.jh.repository.OrganizationRepository;
import com.dialaya.jh.service.OrganizationService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Organization}.
 */
@Service
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

    private final Logger log = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    private final OrganizationRepository organizationRepository;

    public OrganizationServiceImpl(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Override
    public Mono<Organization> save(Organization organization) {
        log.debug("Request to save Organization : {}", organization);
        return organizationRepository.save(organization);
    }

    @Override
    public Mono<Organization> partialUpdate(Organization organization) {
        log.debug("Request to partially update Organization : {}", organization);

        return organizationRepository
            .findById(organization.getId())
            .map(
                existingOrganization -> {
                    if (organization.getName() != null) {
                        existingOrganization.setName(organization.getName());
                    }
                    if (organization.getDescription() != null) {
                        existingOrganization.setDescription(organization.getDescription());
                    }
                    if (organization.getBaseRate() != null) {
                        existingOrganization.setBaseRate(organization.getBaseRate());
                    }
                    if (organization.getGroupRate() != null) {
                        existingOrganization.setGroupRate(organization.getGroupRate());
                    }
                    if (organization.getFullRate() != null) {
                        existingOrganization.setFullRate(organization.getFullRate());
                    }

                    return existingOrganization;
                }
            )
            .flatMap(organizationRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Organization> findAll() {
        log.debug("Request to get all Organizations");
        return organizationRepository.findAll();
    }

    public Mono<Long> countAll() {
        return organizationRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Organization> findOne(Long id) {
        log.debug("Request to get Organization : {}", id);
        return organizationRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Organization : {}", id);
        return organizationRepository.deleteById(id);
    }
}
