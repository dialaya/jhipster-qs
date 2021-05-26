package com.dialaya.jh.service;

import com.dialaya.jh.domain.Organization;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Organization}.
 */
public interface OrganizationService {
    /**
     * Save a organization.
     *
     * @param organization the entity to save.
     * @return the persisted entity.
     */
    Mono<Organization> save(Organization organization);

    /**
     * Partially updates a organization.
     *
     * @param organization the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Organization> partialUpdate(Organization organization);

    /**
     * Get all the organizations.
     *
     * @return the list of entities.
     */
    Flux<Organization> findAll();

    /**
     * Returns the number of organizations available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" organization.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Organization> findOne(Long id);

    /**
     * Delete the "id" organization.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
