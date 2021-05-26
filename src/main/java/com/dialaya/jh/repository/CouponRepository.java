package com.dialaya.jh.repository;

import com.dialaya.jh.domain.Coupon;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Coupon entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CouponRepository extends R2dbcRepository<Coupon, Long>, CouponRepositoryInternal {
    @Query("SELECT * FROM coupon entity WHERE entity.order_id = :id")
    Flux<Coupon> findByOrder(Long id);

    @Query("SELECT * FROM coupon entity WHERE entity.order_id IS NULL")
    Flux<Coupon> findAllWhereOrderIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Coupon> findAll();

    @Override
    Mono<Coupon> findById(Long id);

    @Override
    <S extends Coupon> Mono<S> save(S entity);
}

interface CouponRepositoryInternal {
    <S extends Coupon> Mono<S> insert(S entity);
    <S extends Coupon> Mono<S> save(S entity);
    Mono<Integer> update(Coupon entity);

    Flux<Coupon> findAll();
    Mono<Coupon> findById(Long id);
    Flux<Coupon> findAllBy(Pageable pageable);
    Flux<Coupon> findAllBy(Pageable pageable, Criteria criteria);
}
