package com.dialaya.jh.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * The Member entity.
 */
@ApiModel(description = "The Member entity.")
@Table("member")
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("title")
    private String title;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @NotNull(message = "must not be null")
    @Column("email")
    private String email;

    @NotNull(message = "must not be null")
    @Column("phone_number")
    private String phoneNumber;

    @Column("request_date")
    private Instant requestDate;

    private Long locationId;

    @Transient
    private Location location;

    @Transient
    @JsonIgnoreProperties(value = { "coupons", "member" }, allowSetters = true)
    private Set<Order> orders = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "organization", "member" }, allowSetters = true)
    private Set<Membership> memberships = new HashSet<>();

    @JsonIgnoreProperties(value = { "location", "orders", "memberships", "legalRepresentative" }, allowSetters = true)
    @Transient
    private Member legalRepresentative;

    @Column("legal_representative_id")
    private Long legalRepresentativeId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Member id(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public Member title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Member firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Member lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public Member email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public Member phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Instant getRequestDate() {
        return this.requestDate;
    }

    public Member requestDate(Instant requestDate) {
        this.requestDate = requestDate;
        return this;
    }

    public void setRequestDate(Instant requestDate) {
        this.requestDate = requestDate;
    }

    public Location getLocation() {
        return this.location;
    }

    public Member location(Location location) {
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

    public Set<Order> getOrders() {
        return this.orders;
    }

    public Member orders(Set<Order> orders) {
        this.setOrders(orders);
        return this;
    }

    public Member addOrder(Order order) {
        this.orders.add(order);
        order.setMember(this);
        return this;
    }

    public Member removeOrder(Order order) {
        this.orders.remove(order);
        order.setMember(null);
        return this;
    }

    public void setOrders(Set<Order> orders) {
        if (this.orders != null) {
            this.orders.forEach(i -> i.setMember(null));
        }
        if (orders != null) {
            orders.forEach(i -> i.setMember(this));
        }
        this.orders = orders;
    }

    public Set<Membership> getMemberships() {
        return this.memberships;
    }

    public Member memberships(Set<Membership> memberships) {
        this.setMemberships(memberships);
        return this;
    }

    public Member addMembership(Membership membership) {
        this.memberships.add(membership);
        membership.setMember(this);
        return this;
    }

    public Member removeMembership(Membership membership) {
        this.memberships.remove(membership);
        membership.setMember(null);
        return this;
    }

    public void setMemberships(Set<Membership> memberships) {
        if (this.memberships != null) {
            this.memberships.forEach(i -> i.setMember(null));
        }
        if (memberships != null) {
            memberships.forEach(i -> i.setMember(this));
        }
        this.memberships = memberships;
    }

    public Member getLegalRepresentative() {
        return this.legalRepresentative;
    }

    public Member legalRepresentative(Member member) {
        this.setLegalRepresentative(member);
        this.legalRepresentativeId = member != null ? member.getId() : null;
        return this;
    }

    public void setLegalRepresentative(Member member) {
        this.legalRepresentative = member;
        this.legalRepresentativeId = member != null ? member.getId() : null;
    }

    public Long getLegalRepresentativeId() {
        return this.legalRepresentativeId;
    }

    public void setLegalRepresentativeId(Long member) {
        this.legalRepresentativeId = member;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Member)) {
            return false;
        }
        return id != null && id.equals(((Member) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Member{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", email='" + getEmail() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", requestDate='" + getRequestDate() + "'" +
            "}";
    }
}
