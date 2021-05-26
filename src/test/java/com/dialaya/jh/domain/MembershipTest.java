package com.dialaya.jh.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.dialaya.jh.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MembershipTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Membership.class);
        Membership membership1 = new Membership();
        membership1.setId(1L);
        Membership membership2 = new Membership();
        membership2.setId(membership1.getId());
        assertThat(membership1).isEqualTo(membership2);
        membership2.setId(2L);
        assertThat(membership1).isNotEqualTo(membership2);
        membership1.setId(null);
        assertThat(membership1).isNotEqualTo(membership2);
    }
}
