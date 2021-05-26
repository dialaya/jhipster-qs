package com.dialaya.jh.repository;

import com.dialaya.jh.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Member entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MemberRepository extends R2dbcRepository<Member, Long>, MemberRepositoryInternal {
    Flux<Member> findAllBy(Pageable pageable);

    @Query("SELECT * FROM member entity WHERE entity.location_id = :id")
    Flux<Member> findByLocation(Long id);

    @Query("SELECT * FROM member entity WHERE entity.location_id IS NULL")
    Flux<Member> findAllWhereLocationIsNull();

    @Query("SELECT * FROM member entity WHERE entity.legal_representative_id = :id")
    Flux<Member> findByLegalRepresentative(Long id);

    @Query("SELECT * FROM member entity WHERE entity.legal_representative_id IS NULL")
    Flux<Member> findAllWhereLegalRepresentativeIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Member> findAll();

    @Override
    Mono<Member> findById(Long id);

    @Override
    <S extends Member> Mono<S> save(S entity);
}

interface MemberRepositoryInternal {
    <S extends Member> Mono<S> insert(S entity);
    <S extends Member> Mono<S> save(S entity);
    Mono<Integer> update(Member entity);

    Flux<Member> findAll();
    Mono<Member> findById(Long id);
    Flux<Member> findAllBy(Pageable pageable);
    Flux<Member> findAllBy(Pageable pageable, Criteria criteria);
}
