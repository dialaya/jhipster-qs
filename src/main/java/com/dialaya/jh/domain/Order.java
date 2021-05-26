package com.dialaya.jh.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Order.
 */
@Table("jhi_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("order_id")
    private String orderId;

    /**
     * A relationship
     */
    @ApiModelProperty(value = "A relationship")
    @Transient
    @JsonIgnoreProperties(value = { "order" }, allowSetters = true)
    private Set<Coupon> coupons = new HashSet<>();

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

    public Order id(Long id) {
        this.id = id;
        return this;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public Order orderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Set<Coupon> getCoupons() {
        return this.coupons;
    }

    public Order coupons(Set<Coupon> coupons) {
        this.setCoupons(coupons);
        return this;
    }

    public Order addCoupon(Coupon coupon) {
        this.coupons.add(coupon);
        coupon.setOrder(this);
        return this;
    }

    public Order removeCoupon(Coupon coupon) {
        this.coupons.remove(coupon);
        coupon.setOrder(null);
        return this;
    }

    public void setCoupons(Set<Coupon> coupons) {
        if (this.coupons != null) {
            this.coupons.forEach(i -> i.setOrder(null));
        }
        if (coupons != null) {
            coupons.forEach(i -> i.setOrder(this));
        }
        this.coupons = coupons;
    }

    public Member getMember() {
        return this.member;
    }

    public Order member(Member member) {
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
        if (!(o instanceof Order)) {
            return false;
        }
        return id != null && id.equals(((Order) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Order{" +
            "id=" + getId() +
            ", orderId='" + getOrderId() + "'" +
            "}";
    }
}
