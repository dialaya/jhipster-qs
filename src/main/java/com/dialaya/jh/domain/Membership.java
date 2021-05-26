package com.dialaya.jh.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Membership.
 */
@Table("membership")
public class Membership implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("member_email")
    private String memberEmail;

    @NotNull(message = "must not be null")
    @Column("organisation_name")
    private String organisationName;

    @JsonIgnoreProperties(value = { "location", "memberships" }, allowSetters = true)
    @Transient
    private Organization organization;

    @Column("organization_id")
    private Long organizationId;

    @JsonIgnoreProperties(value = { "location", "orders", "memberships", "legalRepresentative" }, allowSetters = true)
    @Transient
    private Member member;

    @Column("member_id")
    private Long memberId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Membership id(Long id) {
        this.id = id;
        return this;
    }

    public String getMemberEmail() {
        return this.memberEmail;
    }

    public Membership memberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
        return this;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public String getOrganisationName() {
        return this.organisationName;
    }

    public Membership organisationName(String organisationName) {
        this.organisationName = organisationName;
        return this;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public Membership organization(Organization organization) {
        this.setOrganization(organization);
        this.organizationId = organization != null ? organization.getId() : null;
        return this;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Member getMember() {
        return this.member;
    }

    public Membership member(Member member) {
        this.setMember(member);
        this.memberId = member != null ? member.getId() : null;
        return this;
    }

    public void setMember(Member member) {
        this.member = member;
        this.memberId = member != null ? member.getId() : null;
    }

    public Long getMemberId() {
        return this.memberId;
    }

    public void setMemberId(Long member) {
        this.memberId = member;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Membership)) {
            return false;
        }
        return id != null && id.equals(((Membership) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Membership{" +
            "id=" + getId() +
            ", memberEmail='" + getMemberEmail() + "'" +
            ", organisationName='" + getOrganisationName() + "'" +
            "}";
    }
}
