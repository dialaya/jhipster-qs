package com.dialaya.jh.repository;

import com.dialaya.jh.domain.Organization;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Organization entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrganizationRepository extends R2dbcRepository<Organization, Long>, OrganizationRepositoryInternal {
    @Query("SELECT * FROM organization entity WHERE entity.location_id = :id")
    Flux<Organization> findByLocation(Long id);

    @Query("SELECT * FROM organization entity WHERE entity.location_id IS NULL")
    Flux<Organization> findAllWhereLocationIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Organization> findAll();

    @Override
    Mono<Organization> findById(Long id);

    @Override
    <S extends Organization> Mono<S> save(S entity);
}

interface OrganizationRepositoryInternal {
    <S extends Organization> Mono<S> insert(S entity);
    <S extends Organization> Mono<S> save(S entity);
    Mono<Integer> update(Organization entity);

    Flux<Organization> findAll();
    Mono<Organization> findById(Long id);
    Flux<Organization> findAllBy(Pageable pageable);
    Flux<Organization> findAllBy(Pageable pageable, Criteria criteria);
}
