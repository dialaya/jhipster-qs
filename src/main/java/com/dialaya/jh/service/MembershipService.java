package com.dialaya.jh.service;

import com.dialaya.jh.domain.Membership;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Membership}.
 */
public interface MembershipService {
    /**
     * Save a membership.
     *
     * @param membership the entity to save.
     * @return the persisted entity.
     */
    Mono<Membership> save(Membership membership);

    /**
     * Partially updates a membership.
     *
     * @param membership the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Membership> partialUpdate(Membership membership);

    /**
     * Get all the memberships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Membership> findAll(Pageable pageable);

    /**
     * Returns the number of memberships available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" membership.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Membership> findOne(Long id);

    /**
     * Delete the "id" membership.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
