package com.dialaya.jh.web.rest;

import com.dialaya.jh.domain.Coupon;
import com.dialaya.jh.repository.CouponRepository;
import com.dialaya.jh.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.dialaya.jh.domain.Coupon}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CouponResource {

    private final Logger log = LoggerFactory.getLogger(CouponResource.class);

    private static final String ENTITY_NAME = "coupon";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CouponRepository couponRepository;

    public CouponResource(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    /**
     * {@code POST  /coupons} : Create a new coupon.
     *
     * @param coupon the coupon to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new coupon, or with status {@code 400 (Bad Request)} if the coupon has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/coupons")
    public Mono<ResponseEntity<Coupon>> createCoupon(@Valid @RequestBody Coupon coupon) throws URISyntaxException {
        log.debug("REST request to save Coupon : {}", coupon);
        if (coupon.getId() != null) {
            throw new BadRequestAlertException("A new coupon cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return couponRepository
            .save(coupon)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/coupons/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /coupons/:id} : Updates an existing coupon.
     *
     * @param id the id of the coupon to save.
     * @param coupon the coupon to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated coupon,
     * or with status {@code 400 (Bad Request)} if the coupon is not valid,
     * or with status {@code 500 (Internal Server Error)} if the coupon couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/coupons/{id}")
    public Mono<ResponseEntity<Coupon>> updateCoupon(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Coupon coupon
    ) throws URISyntaxException {
        log.debug("REST request to update Coupon : {}, {}", id, coupon);
        if (coupon.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, coupon.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return couponRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return couponRepository
                        .save(coupon)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            result ->
                                ResponseEntity
                                    .ok()
                                    .headers(
                                        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString())
                                    )
                                    .body(result)
                        );
                }
            );
    }

    /**
     * {@code PATCH  /coupons/:id} : Partial updates given fields of an existing coupon, field will ignore if it is null
     *
     * @param id the id of the coupon to save.
     * @param coupon the coupon to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated coupon,
     * or with status {@code 400 (Bad Request)} if the coupon is not valid,
     * or with status {@code 404 (Not Found)} if the coupon is not found,
     * or with status {@code 500 (Internal Server Error)} if the coupon couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/coupons/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<Coupon>> partialUpdateCoupon(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Coupon coupon
    ) throws URISyntaxException {
        log.debug("REST request to partial update Coupon partially : {}, {}", id, coupon);
        if (coupon.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, coupon.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return couponRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<Coupon> result = couponRepository
                        .findById(coupon.getId())
                        .map(
                            existingCoupon -> {
                                if (coupon.getCouponId() != null) {
                                    existingCoupon.setCouponId(coupon.getCouponId());
                                }
                                if (coupon.getOffRate() != null) {
                                    existingCoupon.setOffRate(coupon.getOffRate());
                                }

                                return existingCoupon;
                            }
                        )
                        .flatMap(couponRepository::save);

                    return result
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            res ->
                                ResponseEntity
                                    .ok()
                                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                                    .body(res)
                        );
                }
            );
    }

    /**
     * {@code GET  /coupons} : get all the coupons.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of coupons in body.
     */
    @GetMapping("/coupons")
    public Mono<List<Coupon>> getAllCoupons() {
        log.debug("REST request to get all Coupons");
        return couponRepository.findAll().collectList();
    }

    /**
     * {@code GET  /coupons} : get all the coupons as a stream.
     * @return the {@link Flux} of coupons.
     */
    @GetMapping(value = "/coupons", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Coupon> getAllCouponsAsStream() {
        log.debug("REST request to get all Coupons as a stream");
        return couponRepository.findAll();
    }

    /**
     * {@code GET  /coupons/:id} : get the "id" coupon.
     *
     * @param id the id of the coupon to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the coupon, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/coupons/{id}")
    public Mono<ResponseEntity<Coupon>> getCoupon(@PathVariable Long id) {
        log.debug("REST request to get Coupon : {}", id);
        Mono<Coupon> coupon = couponRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(coupon);
    }

    /**
     * {@code DELETE  /coupons/:id} : delete the "id" coupon.
     *
     * @param id the id of the coupon to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/coupons/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCoupon(@PathVariable Long id) {
        log.debug("REST request to delete Coupon : {}", id);
        return couponRepository
            .deleteById(id)
            .map(
                result ->
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
            );
    }
}
