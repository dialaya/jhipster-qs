package com.dialaya.jh.web.rest;

import com.dialaya.jh.domain.Member;
import com.dialaya.jh.repository.MemberRepository;
import com.dialaya.jh.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.dialaya.jh.domain.Member}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class MemberResource {

    private final Logger log = LoggerFactory.getLogger(MemberResource.class);

    private static final String ENTITY_NAME = "member";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MemberRepository memberRepository;

    public MemberResource(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * {@code POST  /members} : Create a new member.
     *
     * @param member the member to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new member, or with status {@code 400 (Bad Request)} if the member has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/members")
    public Mono<ResponseEntity<Member>> createMember(@Valid @RequestBody Member member) throws URISyntaxException {
        log.debug("REST request to save Member : {}", member);
        if (member.getId() != null) {
            throw new BadRequestAlertException("A new member cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return memberRepository
            .save(member)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/members/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /members/:id} : Updates an existing member.
     *
     * @param id the id of the member to save.
     * @param member the member to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated member,
     * or with status {@code 400 (Bad Request)} if the member is not valid,
     * or with status {@code 500 (Internal Server Error)} if the member couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/members/{id}")
    public Mono<ResponseEntity<Member>> updateMember(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Member member
    ) throws URISyntaxException {
        log.debug("REST request to update Member : {}, {}", id, member);
        if (member.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, member.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return memberRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return memberRepository
                        .save(member)
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
     * {@code PATCH  /members/:id} : Partial updates given fields of an existing member, field will ignore if it is null
     *
     * @param id the id of the member to save.
     * @param member the member to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated member,
     * or with status {@code 400 (Bad Request)} if the member is not valid,
     * or with status {@code 404 (Not Found)} if the member is not found,
     * or with status {@code 500 (Internal Server Error)} if the member couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/members/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<Member>> partialUpdateMember(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Member member
    ) throws URISyntaxException {
        log.debug("REST request to partial update Member partially : {}, {}", id, member);
        if (member.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, member.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return memberRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<Member> result = memberRepository
                        .findById(member.getId())
                        .map(
                            existingMember -> {
                                if (member.getTitle() != null) {
                                    existingMember.setTitle(member.getTitle());
                                }
                                if (member.getFirstName() != null) {
                                    existingMember.setFirstName(member.getFirstName());
                                }
                                if (member.getLastName() != null) {
                                    existingMember.setLastName(member.getLastName());
                                }
                                if (member.getEmail() != null) {
                                    existingMember.setEmail(member.getEmail());
                                }
                                if (member.getPhoneNumber() != null) {
                                    existingMember.setPhoneNumber(member.getPhoneNumber());
                                }
                                if (member.getRequestDate() != null) {
                                    existingMember.setRequestDate(member.getRequestDate());
                                }

                                return existingMember;
                            }
                        )
                        .flatMap(memberRepository::save);

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
     * {@code GET  /members} : get all the members.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of members in body.
     */
    @GetMapping("/members")
    public Mono<ResponseEntity<List<Member>>> getAllMembers(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Members");
        return memberRepository
            .count()
            .zipWith(memberRepository.findAllBy(pageable).collectList())
            .map(
                countWithEntities -> {
                    return ResponseEntity
                        .ok()
                        .headers(
                            PaginationUtil.generatePaginationHttpHeaders(
                                UriComponentsBuilder.fromHttpRequest(request),
                                new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                            )
                        )
                        .body(countWithEntities.getT2());
                }
            );
    }

    /**
     * {@code GET  /members/:id} : get the "id" member.
     *
     * @param id the id of the member to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the member, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/members/{id}")
    public Mono<ResponseEntity<Member>> getMember(@PathVariable Long id) {
        log.debug("REST request to get Member : {}", id);
        Mono<Member> member = memberRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(member);
    }

    /**
     * {@code DELETE  /members/:id} : delete the "id" member.
     *
     * @param id the id of the member to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/members/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteMember(@PathVariable Long id) {
        log.debug("REST request to delete Member : {}", id);
        return memberRepository
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
