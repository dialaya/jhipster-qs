package com.dialaya.jh.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * The organization entity.
 */
@ApiModel(description = "The organization entity.")
@Table("organization")
public class Organization implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("base_rate")
    private Long baseRate;

    @Column("group_rate")
    private Long groupRate;

    @Column("full_rate")
    private Long fullRate;

    private Long locationId;

    @Transient
    private Location location;

    @Transient
    @JsonIgnoreProperties(value = { "organization", "member" }, allowSetters = true)
    private Set<Membership> memberships = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Organization id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Organization name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Organization description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getBaseRate() {
        return this.baseRate;
    }

    public Organization baseRate(Long baseRate) {
        this.baseRate = baseRate;
        return this;
    }

    public void setBaseRate(Long baseRate) {
        this.baseRate = baseRate;
    }

    public Long getGroupRate() {
        return this.groupRate;
    }

    public Organization groupRate(Long groupRate) {
        this.groupRate = groupRate;
        return this;
    }

    public void setGroupRate(Long groupRate) {
        this.groupRate = groupRate;
    }

    public Long getFullRate() {
        return this.fullRate;
    }

    public Organization fullRate(Long fullRate) {
        this.fullRate = fullRate;
        return this;
    }

    public void setFullRate(Long fullRate) {
        this.fullRate = fullRate;
    }

    public Location getLocation() {
        return this.location;
    }

    public Organization location(Location location) {
        this.setLocation(location);
        this.locationId = location != null ? location.getId() : null;
        return this;
    }

    public void setLocation(Location location) {
        this.location = location;
        this.locationId = location != null ? location.getId() : null;
    }

    public Long getLocationId() {
        return this.locationId;
    }

    public void setLocationId(Long location) {
        this.locationId = location;
    }

    public Set<Membership> getMemberships() {
        return this.memberships;
    }

    public Organization memberships(Set<Membership> memberships) {
        this.setMemberships(memberships);
        return this;
    }

    public Organization addMembership(Membership membership) {
        this.memberships.add(membership);
        membership.setOrganization(this);
        return this;
    }

    public Organization removeMembership(Membership membership) {
        this.memberships.remove(membership);
        membership.setOrganization(null);
        return this;
    }

    public void setMemberships(Set<Membership> memberships) {
        if (this.memberships != null) {
            this.memberships.forEach(i -> i.setOrganization(null));
        }
        if (memberships != null) {
            memberships.forEach(i -> i.setOrganization(this));
        }
        this.memberships = memberships;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Organization)) {
            return false;
        }
        return id != null && id.equals(((Organization) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Organization{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", baseRate=" + getBaseRate() +
            ", groupRate=" + getGroupRate() +
            ", fullRate=" + getFullRate() +
            "}";
    }
}
