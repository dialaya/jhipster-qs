package com.dialaya.jh.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.dialaya.jh.IntegrationTest;
import com.dialaya.jh.domain.Coupon;
import com.dialaya.jh.repository.CouponRepository;
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
 * Integration tests for the {@link CouponResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class CouponResourceIT {

    private static final String DEFAULT_COUPON_ID = "AAAAAAAAAA";
    private static final String UPDATED_COUPON_ID = "BBBBBBBBBB";

    private static final Long DEFAULT_OFF_RATE = 1L;
    private static final Long UPDATED_OFF_RATE = 2L;

    private static final String ENTITY_API_URL = "/api/coupons";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Coupon coupon;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Coupon createEntity(EntityManager em) {
        Coupon coupon = new Coupon().couponId(DEFAULT_COUPON_ID).offRate(DEFAULT_OFF_RATE);
        return coupon;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Coupon createUpdatedEntity(EntityManager em) {
        Coupon coupon = new Coupon().couponId(UPDATED_COUPON_ID).offRate(UPDATED_OFF_RATE);
        return coupon;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Coupon.class).block();
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
        coupon = createEntity(em);
    }

    @Test
    void createCoupon() throws Exception {
        int databaseSizeBeforeCreate = couponRepository.findAll().collectList().block().size();
        // Create the Coupon
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(coupon))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Coupon in the database
        List<Coupon> couponList = couponRepository.findAll().collectList().block();
        assertThat(couponList).hasSize(databaseSizeBeforeCreate + 1);
        Coupon testCoupon = couponList.get(couponList.size() - 1);
        assertThat(testCoupon.getCouponId()).isEqualTo(DEFAULT_COUPON_ID);
        assertThat(testCoupon.getOffRate()).isEqualTo(DEFAULT_OFF_RATE);
    }

    @Test
    void createCouponWithExistingId() throws Exception {
        // Create the Coupon with an existing ID
        coupon.setId(1L);

        int databaseSizeBeforeCreate = couponRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(coupon))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Coupon in the database
        List<Coupon> couponList = couponRepository.findAll().collectList().block();
        assertThat(couponList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkCouponIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = couponRepository.findAll().collectList().block().size();
        // set the field null
        coupon.setCouponId(null);

        // Create the Coupon, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(coupon))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Coupon> couponList = couponRepository.findAll().collectList().block();
        assertThat(couponList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkOffRateIsRequired() throws Exception {
        int databaseSizeBeforeTest = couponRepository.findAll().collectList().block().size();
        // set the field null
        coupon.setOffRate(null);

        // Create the Coupon, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(coupon))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Coupon> couponList = couponRepository.findAll().collectList().block();
        assertThat(couponList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCouponsAsStream() {
        // Initialize the database
        couponRepository.save(coupon).block();

        List<Coupon> couponList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Coupon.class)
            .getResponseBody()
            .filter(coupon::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(couponList).isNotNull();
        assertThat(couponList).hasSize(1);
        Coupon testCoupon = couponList.get(0);
        assertThat(testCoupon.getCouponId()).isEqualTo(DEFAULT_COUPON_ID);
        assertThat(testCoupon.getOffRate()).isEqualTo(DEFAULT_OFF_RATE);
    }

    @Test
    void getAllCoupons() {
        // Initialize the database
        couponRepository.save(coupon).block();

        // Get all the couponList
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
            .value(hasItem(coupon.getId().intValue()))
            .jsonPath("$.[*].couponId")
            .value(hasItem(DEFAULT_COUPON_ID))
            .jsonPath("$.[*].offRate")
            .value(hasItem(DEFAULT_OFF_RATE.intValue()));
    }

    @Test
    void getCoupon() {
        // Initialize the database
        couponRepository.save(coupon).block();

        // Get the coupon
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, coupon.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(coupon.getId().intValue()))
            .jsonPath("$.couponId")
            .value(is(DEFAULT_COUPON_ID))
            .jsonPath("$.offRate")
            .value(is(DEFAULT_OFF_RATE.intValue()));
    }

    @Test
    void getNonExistingCoupon() {
        // Get the coupon
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCoupon() throws Exception {
        // Initialize the database
        couponRepository.save(coupon).block();

        int databaseSizeBeforeUpdate = couponRepository.findAll().collectList().block().size();

        // Update the coupon
        Coupon updatedCoupon = couponRepository.findById(coupon.getId()).block();
        updatedCoupon.couponId(UPDATED_COUPON_ID).offRate(UPDATED_OFF_RATE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCoupon.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCoupon))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Coupon in the database
        List<Coupon> couponList = couponRepository.findAll().collectList().block();
        assertThat(couponList).hasSize(databaseSizeBeforeUpdate);
        Coupon testCoupon = couponList.get(couponList.size() - 1);
        assertThat(testCoupon.getCouponId()).isEqualTo(UPDATED_COUPON_ID);
        assertThat(testCoupon.getOffRate()).isEqualTo(UPDATED_OFF_RATE);
    }

    @Test
    void putNonExistingCoupon() throws Exception {
        int databaseSizeBeforeUpdate = couponRepository.findAll().collectList().block().size();
        coupon.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, coupon.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(coupon))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Coupon in the database
        List<Coupon> couponList = couponRepository.findAll().collectList().block();
        assertThat(couponList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCoupon() throws Exception {
        int databaseSizeBeforeUpdate = couponRepository.findAll().collectList().block().size();
        coupon.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(coupon))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Coupon in the database
        List<Coupon> couponList = couponRepository.findAll().collectList().block();
        assertThat(couponList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCoupon() throws Exception {
        int databaseSizeBeforeUpdate = couponRepository.findAll().collectList().block().size();
        coupon.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(coupon))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Coupon in the database
        List<Coupon> couponList = couponRepository.findAll().collectList().block();
        assertThat(couponList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCouponWithPatch() throws Exception {
        // Initialize the database
        couponRepository.save(coupon).block();

        int databaseSizeBeforeUpdate = couponRepository.findAll().collectList().block().size();

        // Update the coupon using partial update
        Coupon partialUpdatedCoupon = new Coupon();
        partialUpdatedCoupon.setId(coupon.getId());

        partialUpdatedCoupon.couponId(UPDATED_COUPON_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCoupon.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCoupon))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Coupon in the database
        List<Coupon> couponList = couponRepository.findAll().collectList().block();
        assertThat(couponList).hasSize(databaseSizeBeforeUpdate);
        Coupon testCoupon = couponList.get(couponList.size() - 1);
        assertThat(testCoupon.getCouponId()).isEqualTo(UPDATED_COUPON_ID);
        assertThat(testCoupon.getOffRate()).isEqualTo(DEFAULT_OFF_RATE);
    }

    @Test
    void fullUpdateCouponWithPatch() throws Exception {
        // Initialize the database
        couponRepository.save(coupon).block();

        int databaseSizeBeforeUpdate = couponRepository.findAll().collectList().block().size();

        // Update the coupon using partial update
        Coupon partialUpdatedCoupon = new Coupon();
        partialUpdatedCoupon.setId(coupon.getId());

        partialUpdatedCoupon.couponId(UPDATED_COUPON_ID).offRate(UPDATED_OFF_RATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCoupon.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCoupon))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Coupon in the database
        List<Coupon> couponList = couponRepository.findAll().collectList().block();
        assertThat(couponList).hasSize(databaseSizeBeforeUpdate);
        Coupon testCoupon = couponList.get(couponList.size() - 1);
        assertThat(testCoupon.getCouponId()).isEqualTo(UPDATED_COUPON_ID);
        assertThat(testCoupon.getOffRate()).isEqualTo(UPDATED_OFF_RATE);
    }

    @Test
    void patchNonExistingCoupon() throws Exception {
        int databaseSizeBeforeUpdate = couponRepository.findAll().collectList().block().size();
        coupon.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, coupon.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(coupon))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Coupon in the database
        List<Coupon> couponList = couponRepository.findAll().collectList().block();
        assertThat(couponList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCoupon() throws Exception {
        int databaseSizeBeforeUpdate = couponRepository.findAll().collectList().block().size();
        coupon.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(coupon))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Coupon in the database
        List<Coupon> couponList = couponRepository.findAll().collectList().block();
        assertThat(couponList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCoupon() throws Exception {
        int databaseSizeBeforeUpdate = couponRepository.findAll().collectList().block().size();
        coupon.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(coupon))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Coupon in the database
        List<Coupon> couponList = couponRepository.findAll().collectList().block();
        assertThat(couponList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCoupon() {
        // Initialize the database
        couponRepository.save(coupon).block();

        int databaseSizeBeforeDelete = couponRepository.findAll().collectList().block().size();

        // Delete the coupon
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, coupon.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Coupon> couponList = couponRepository.findAll().collectList().block();
        assertThat(couponList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
