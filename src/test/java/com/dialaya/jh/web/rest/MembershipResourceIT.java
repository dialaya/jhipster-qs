package com.dialaya.jh.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.dialaya.jh.IntegrationTest;
import com.dialaya.jh.domain.Membership;
import com.dialaya.jh.repository.MembershipRepository;
import com.dialaya.jh.service.EntityManager;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link MembershipResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class MembershipResourceIT {

    private static final String DEFAULT_MEMBER_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_MEMBER_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_ORGANISATION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ORGANISATION_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/memberships";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Membership membership;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Membership createEntity(EntityManager em) {
        Membership membership = new Membership().memberEmail(DEFAULT_MEMBER_EMAIL).organisationName(DEFAULT_ORGANISATION_NAME);
        return membership;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Membership createUpdatedEntity(EntityManager em) {
        Membership membership = new Membership().memberEmail(UPDATED_MEMBER_EMAIL).organisationName(UPDATED_ORGANISATION_NAME);
        return membership;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Membership.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        membership = createEntity(em);
    }

    @Test
    void createMembership() throws Exception {
        int databaseSizeBeforeCreate = membershipRepository.findAll().collectList().block().size();
        // Create the Membership
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(membership))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Membership in the database
        List<Membership> membershipList = membershipRepository.findAll().collectList().block();
        assertThat(membershipList).hasSize(databaseSizeBeforeCreate + 1);
        Membership testMembership = membershipList.get(membershipList.size() - 1);
        assertThat(testMembership.getMemberEmail()).isEqualTo(DEFAULT_MEMBER_EMAIL);
        assertThat(testMembership.getOrganisationName()).isEqualTo(DEFAULT_ORGANISATION_NAME);
    }

    @Test
    void createMembershipWithExistingId() throws Exception {
        // Create the Membership with an existing ID
        membership.setId(1L);

        int databaseSizeBeforeCreate = membershipRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(membership))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Membership in the database
        List<Membership> membershipList = membershipRepository.findAll().collectList().block();
        assertThat(membershipList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkMemberEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = membershipRepository.findAll().collectList().block().size();
        // set the field null
        membership.setMemberEmail(null);

        // Create the Membership, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(membership))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Membership> membershipList = membershipRepository.findAll().collectList().block();
        assertThat(membershipList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkOrganisationNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = membershipRepository.findAll().collectList().block().size();
        // set the field null
        membership.setOrganisationName(null);

        // Create the Membership, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(membership))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Membership> membershipList = membershipRepository.findAll().collectList().block();
        assertThat(membershipList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllMemberships() {
        // Initialize the database
        membershipRepository.save(membership).block();

        // Get all the membershipList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(membership.getId().intValue()))
            .jsonPath("$.[*].memberEmail")
            .value(hasItem(DEFAULT_MEMBER_EMAIL))
            .jsonPath("$.[*].organisationName")
            .value(hasItem(DEFAULT_ORGANISATION_NAME));
    }

    @Test
    void getMembership() {
        // Initialize the database
        membershipRepository.save(membership).block();

        // Get the membership
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, membership.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(membership.getId().intValue()))
            .jsonPath("$.memberEmail")
            .value(is(DEFAULT_MEMBER_EMAIL))
            .jsonPath("$.organisationName")
            .value(is(DEFAULT_ORGANISATION_NAME));
    }

    @Test
    void getNonExistingMembership() {
        // Get the membership
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewMembership() throws Exception {
        // Initialize the database
        membershipRepository.save(membership).block();

        int databaseSizeBeforeUpdate = membershipRepository.findAll().collectList().block().size();

        // Update the membership
        Membership updatedMembership = membershipRepository.findById(membership.getId()).block();
        updatedMembership.memberEmail(UPDATED_MEMBER_EMAIL).organisationName(UPDATED_ORGANISATION_NAME);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedMembership.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedMembership))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Membership in the database
        List<Membership> membershipList = membershipRepository.findAll().collectList().block();
        assertThat(membershipList).hasSize(databaseSizeBeforeUpdate);
        Membership testMembership = membershipList.get(membershipList.size() - 1);
        assertThat(testMembership.getMemberEmail()).isEqualTo(UPDATED_MEMBER_EMAIL);
        assertThat(testMembership.getOrganisationName()).isEqualTo(UPDATED_ORGANISATION_NAME);
    }

    @Test
    void putNonExistingMembership() throws Exception {
        int databaseSizeBeforeUpdate = membershipRepository.findAll().collectList().block().size();
        membership.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, membership.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(membership))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Membership in the database
        List<Membership> membershipList = membershipRepository.findAll().collectList().block();
        assertThat(membershipList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchMembership() throws Exception {
        int databaseSizeBeforeUpdate = membershipRepository.findAll().collectList().block().size();
        membership.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(membership))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Membership in the database
        List<Membership> membershipList = membershipRepository.findAll().collectList().block();
        assertThat(membershipList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamMembership() throws Exception {
        int databaseSizeBeforeUpdate = membershipRepository.findAll().collectList().block().size();
        membership.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(membership))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Membership in the database
        List<Membership> membershipList = membershipRepository.findAll().collectList().block();
        assertThat(membershipList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateMembershipWithPatch() throws Exception {
        // Initialize the database
        membershipRepository.save(membership).block();

        int databaseSizeBeforeUpdate = membershipRepository.findAll().collectList().block().size();

        // Update the membership using partial update
        Membership partialUpdatedMembership = new Membership();
        partialUpdatedMembership.setId(membership.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMembership.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedMembership))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Membership in the database
        List<Membership> membershipList = membershipRepository.findAll().collectList().block();
        assertThat(membershipList).hasSize(databaseSizeBeforeUpdate);
        Membership testMembership = membershipList.get(membershipList.size() - 1);
        assertThat(testMembership.getMemberEmail()).isEqualTo(DEFAULT_MEMBER_EMAIL);
        assertThat(testMembership.getOrganisationName()).isEqualTo(DEFAULT_ORGANISATION_NAME);
    }

    @Test
    void fullUpdateMembershipWithPatch() throws Exception {
        // Initialize the database
        membershipRepository.save(membership).block();

        int databaseSizeBeforeUpdate = membershipRepository.findAll().collectList().block().size();

        // Update the membership using partial update
        Membership partialUpdatedMembership = new Membership();
        partialUpdatedMembership.setId(membership.getId());

        partialUpdatedMembership.memberEmail(UPDATED_MEMBER_EMAIL).organisationName(UPDATED_ORGANISATION_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMembership.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedMembership))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Membership in the database
        List<Membership> membershipList = membershipRepository.findAll().collectList().block();
        assertThat(membershipList).hasSize(databaseSizeBeforeUpdate);
        Membership testMembership = membershipList.get(membershipList.size() - 1);
        assertThat(testMembership.getMemberEmail()).isEqualTo(UPDATED_MEMBER_EMAIL);
        assertThat(testMembership.getOrganisationName()).isEqualTo(UPDATED_ORGANISATION_NAME);
    }

    @Test
    void patchNonExistingMembership() throws Exception {
        int databaseSizeBeforeUpdate = membershipRepository.findAll().collectList().block().size();
        membership.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, membership.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(membership))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Membership in the database
        List<Membership> membershipList = membershipRepository.findAll().collectList().block();
        assertThat(membershipList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchMembership() throws Exception {
        int databaseSizeBeforeUpdate = membershipRepository.findAll().collectList().block().size();
        membership.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(membership))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Membership in the database
        List<Membership> membershipList = membershipRepository.findAll().collectList().block();
        assertThat(membershipList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamMembership() throws Exception {
        int databaseSizeBeforeUpdate = membershipRepository.findAll().collectList().block().size();
        membership.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(membership))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Membership in the database
        List<Membership> membershipList = membershipRepository.findAll().collectList().block();
        assertThat(membershipList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteMembership() {
        // Initialize the database
        membershipRepository.save(membership).block();

        int databaseSizeBeforeDelete = membershipRepository.findAll().collectList().block().size();

        // Delete the membership
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, membership.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Membership> membershipList = membershipRepository.findAll().collectList().block();
        assertThat(membershipList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
