package com.dialaya.jh.repository;

import com.dialaya.jh.domain.Membership;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Membership entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MembershipRepository extends R2dbcRepository<Membership, Long>, MembershipRepositoryInternal {
    Flux<Membership> findAllBy(Pageable pageable);

    @Query("SELECT * FROM membership entity WHERE entity.organization_id = :id")
    Flux<Membership> findByOrganization(Long id);

    @Query("SELECT * FROM membership entity WHERE entity.organization_id IS NULL")
    Flux<Membership> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM membership entity WHERE entity.member_id = :id")
    Flux<Membership> findByMember(Long id);

    @Query("SELECT * FROM membership entity WHERE entity.member_id IS NULL")
    Flux<Membership> findAllWhereMemberIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Membership> findAll();

    @Override
    Mono<Membership> findById(Long id);

    @Override
    <S extends Membership> Mono<S> save(S entity);
}

interface MembershipRepositoryInternal {
    <S extends Membership> Mono<S> insert(S entity);
    <S extends Membership> Mono<S> save(S entity);
    Mono<Integer> update(Membership entity);

    Flux<Membership> findAll();
    Mono<Membership> findById(Long id);
    Flux<Membership> findAllBy(Pageable pageable);
    Flux<Membership> findAllBy(Pageable pageable, Criteria criteria);
}
