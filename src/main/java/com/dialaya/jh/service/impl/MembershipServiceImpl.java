package com.dialaya.jh.service.impl;

import com.dialaya.jh.domain.Membership;
import com.dialaya.jh.repository.MembershipRepository;
import com.dialaya.jh.service.MembershipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Membership}.
 */
@Service
@Transactional
public class MembershipServiceImpl implements MembershipService {

    private final Logger log = LoggerFactory.getLogger(MembershipServiceImpl.class);

    private final MembershipRepository membershipRepository;

    public MembershipServiceImpl(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    @Override
    public Mono<Membership> save(Membership membership) {
        log.debug("Request to save Membership : {}", membership);
        return membershipRepository.save(membership);
    }

    @Override
    public Mono<Membership> partialUpdate(Membership membership) {
        log.debug("Request to partially update Membership : {}", membership);

        return membershipRepository
            .findById(membership.getId())
            .map(
                existingMembership -> {
                    if (membership.getMemberEmail() != null) {
                        existingMembership.setMemberEmail(membership.getMemberEmail());
                    }
                    if (membership.getOrganisationName() != null) {
                        existingMembership.setOrganisationName(membership.getOrganisationName());
                    }

                    return existingMembership;
                }
            )
            .flatMap(membershipRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Membership> findAll(Pageable pageable) {
        log.debug("Request to get all Memberships");
        return membershipRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return membershipRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Membership> findOne(Long id) {
        log.debug("Request to get Membership : {}", id);
        return membershipRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Membership : {}", id);
        return membershipRepository.deleteById(id);
    }
}
