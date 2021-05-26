package com.dialaya.jh.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.dialaya.jh.IntegrationTest;
import com.dialaya.jh.domain.Organization;
import com.dialaya.jh.repository.OrganizationRepository;
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
 * Integration tests for the {@link OrganizationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class OrganizationResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Long DEFAULT_BASE_RATE = 1L;
    private static final Long UPDATED_BASE_RATE = 2L;

    private static final Long DEFAULT_GROUP_RATE = 1L;
    private static final Long UPDATED_GROUP_RATE = 2L;

    private static final Long DEFAULT_FULL_RATE = 1L;
    private static final Long UPDATED_FULL_RATE = 2L;

    private static final String ENTITY_API_URL = "/api/organizations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Organization organization;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organization createEntity(EntityManager em) {
        Organization organization = new Organization()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .baseRate(DEFAULT_BASE_RATE)
            .groupRate(DEFAULT_GROUP_RATE)
            .fullRate(DEFAULT_FULL_RATE);
        return organization;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organization createUpdatedEntity(EntityManager em) {
        Organization organization = new Organization()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .baseRate(UPDATED_BASE_RATE)
            .groupRate(UPDATED_GROUP_RATE)
            .fullRate(UPDATED_FULL_RATE);
        return organization;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Organization.class).block();
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
        organization = createEntity(em);
    }

    @Test
    void createOrganization() throws Exception {
        int databaseSizeBeforeCreate = organizationRepository.findAll().collectList().block().size();
        // Create the Organization
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organization))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll().collectList().block();
        assertThat(organizationList).hasSize(databaseSizeBeforeCreate + 1);
        Organization testOrganization = organizationList.get(organizationList.size() - 1);
        assertThat(testOrganization.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOrganization.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testOrganization.getBaseRate()).isEqualTo(DEFAULT_BASE_RATE);
        assertThat(testOrganization.getGroupRate()).isEqualTo(DEFAULT_GROUP_RATE);
        assertThat(testOrganization.getFullRate()).isEqualTo(DEFAULT_FULL_RATE);
    }

    @Test
    void createOrganizationWithExistingId() throws Exception {
        // Create the Organization with an existing ID
        organization.setId(1L);

        int databaseSizeBeforeCreate = organizationRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organization))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll().collectList().block();
        assertThat(organizationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = organizationRepository.findAll().collectList().block().size();
        // set the field null
        organization.setName(null);

        // Create the Organization, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organization))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Organization> organizationList = organizationRepository.findAll().collectList().block();
        assertThat(organizationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllOrganizationsAsStream() {
        // Initialize the database
        organizationRepository.save(organization).block();

        List<Organization> organizationList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Organization.class)
            .getResponseBody()
            .filter(organization::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(organizationList).isNotNull();
        assertThat(organizationList).hasSize(1);
        Organization testOrganization = organizationList.get(0);
        assertThat(testOrganization.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOrganization.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testOrganization.getBaseRate()).isEqualTo(DEFAULT_BASE_RATE);
        assertThat(testOrganization.getGroupRate()).isEqualTo(DEFAULT_GROUP_RATE);
        assertThat(testOrganization.getFullRate()).isEqualTo(DEFAULT_FULL_RATE);
    }

    @Test
    void getAllOrganizations() {
        // Initialize the database
        organizationRepository.save(organization).block();

        // Get all the organizationList
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
            .value(hasItem(organization.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].baseRate")
            .value(hasItem(DEFAULT_BASE_RATE.intValue()))
            .jsonPath("$.[*].groupRate")
            .value(hasItem(DEFAULT_GROUP_RATE.intValue()))
            .jsonPath("$.[*].fullRate")
            .value(hasItem(DEFAULT_FULL_RATE.intValue()));
    }

    @Test
    void getOrganization() {
        // Initialize the database
        organizationRepository.save(organization).block();

        // Get the organization
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, organization.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(organization.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.baseRate")
            .value(is(DEFAULT_BASE_RATE.intValue()))
            .jsonPath("$.groupRate")
            .value(is(DEFAULT_GROUP_RATE.intValue()))
            .jsonPath("$.fullRate")
            .value(is(DEFAULT_FULL_RATE.intValue()));
    }

    @Test
    void getNonExistingOrganization() {
        // Get the organization
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewOrganization() throws Exception {
        // Initialize the database
        organizationRepository.save(organization).block();

        int databaseSizeBeforeUpdate = organizationRepository.findAll().collectList().block().size();

        // Update the organization
        Organization updatedOrganization = organizationRepository.findById(organization.getId()).block();
        updatedOrganization
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .baseRate(UPDATED_BASE_RATE)
            .groupRate(UPDATED_GROUP_RATE)
            .fullRate(UPDATED_FULL_RATE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedOrganization.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedOrganization))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll().collectList().block();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
        Organization testOrganization = organizationList.get(organizationList.size() - 1);
        assertThat(testOrganization.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOrganization.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testOrganization.getBaseRate()).isEqualTo(UPDATED_BASE_RATE);
        assertThat(testOrganization.getGroupRate()).isEqualTo(UPDATED_GROUP_RATE);
        assertThat(testOrganization.getFullRate()).isEqualTo(UPDATED_FULL_RATE);
    }

    @Test
    void putNonExistingOrganization() throws Exception {
        int databaseSizeBeforeUpdate = organizationRepository.findAll().collectList().block().size();
        organization.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, organization.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organization))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll().collectList().block();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchOrganization() throws Exception {
        int databaseSizeBeforeUpdate = organizationRepository.findAll().collectList().block().size();
        organization.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organization))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll().collectList().block();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamOrganization() throws Exception {
        int databaseSizeBeforeUpdate = organizationRepository.findAll().collectList().block().size();
        organization.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(organization))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll().collectList().block();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateOrganizationWithPatch() throws Exception {
        // Initialize the database
        organizationRepository.save(organization).block();

        int databaseSizeBeforeUpdate = organizationRepository.findAll().collectList().block().size();

        // Update the organization using partial update
        Organization partialUpdatedOrganization = new Organization();
        partialUpdatedOrganization.setId(organization.getId());

        partialUpdatedOrganization.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrganization.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOrganization))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll().collectList().block();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
        Organization testOrganization = organizationList.get(organizationList.size() - 1);
        assertThat(testOrganization.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOrganization.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testOrganization.getBaseRate()).isEqualTo(DEFAULT_BASE_RATE);
        assertThat(testOrganization.getGroupRate()).isEqualTo(DEFAULT_GROUP_RATE);
        assertThat(testOrganization.getFullRate()).isEqualTo(DEFAULT_FULL_RATE);
    }

    @Test
    void fullUpdateOrganizationWithPatch() throws Exception {
        // Initialize the database
        organizationRepository.save(organization).block();

        int databaseSizeBeforeUpdate = organizationRepository.findAll().collectList().block().size();

        // Update the organization using partial update
        Organization partialUpdatedOrganization = new Organization();
        partialUpdatedOrganization.setId(organization.getId());

        partialUpdatedOrganization
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .baseRate(UPDATED_BASE_RATE)
            .groupRate(UPDATED_GROUP_RATE)
            .fullRate(UPDATED_FULL_RATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrganization.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOrganization))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll().collectList().block();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
        Organization testOrganization = organizationList.get(organizationList.size() - 1);
        assertThat(testOrganization.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOrganization.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testOrganization.getBaseRate()).isEqualTo(UPDATED_BASE_RATE);
        assertThat(testOrganization.getGroupRate()).isEqualTo(UPDATED_GROUP_RATE);
        assertThat(testOrganization.getFullRate()).isEqualTo(UPDATED_FULL_RATE);
    }

    @Test
    void patchNonExistingOrganization() throws Exception {
        int databaseSizeBeforeUpdate = organizationRepository.findAll().collectList().block().size();
        organization.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, organization.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(organization))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll().collectList().block();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchOrganization() throws Exception {
        int databaseSizeBeforeUpdate = organizationRepository.findAll().collectList().block().size();
        organization.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(organization))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll().collectList().block();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamOrganization() throws Exception {
        int databaseSizeBeforeUpdate = organizationRepository.findAll().collectList().block().size();
        organization.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(organization))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll().collectList().block();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteOrganization() {
        // Initialize the database
        organizationRepository.save(organization).block();

        int databaseSizeBeforeDelete = organizationRepository.findAll().collectList().block().size();

        // Delete the organization
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, organization.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Organization> organizationList = organizationRepository.findAll().collectList().block();
        assertThat(organizationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
