package com.dialaya.jh.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Coupon.
 */
@Table("coupon")
public class Coupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("coupon_id")
    private String couponId;

    @NotNull(message = "must not be null")
    @Column("off_rate")
    private Long offRate;

    /**
     * Another side of the same relationship
     */
    @ApiModelProperty(value = "Another side of the same relationship")
    @JsonIgnoreProperties(value = { "coupons", "member" }, allowSetters = true)
    @Transient
    private Order order;

    @Column("order_id")
    private Long orderId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Coupon id(Long id) {
        this.id = id;
        return this;
    }

    public String getCouponId() {
        return this.couponId;
    }

    public Coupon couponId(String couponId) {
        this.couponId = couponId;
        return this;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public Long getOffRate() {
        return this.offRate;
    }

    public Coupon offRate(Long offRate) {
        this.offRate = offRate;
        return this;
    }

    public void setOffRate(Long offRate) {
        this.offRate = offRate;
    }

    public Order getOrder() {
        return this.order;
    }

    public Coupon order(Order order) {
        this.setOrder(order);
        this.orderId = order != null ? order.getId() : null;
        return this;
    }

    public void setOrder(Order order) {
        this.order = order;
        this.orderId = order != null ? order.getId() : null;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public void setOrderId(Long order) {
        this.orderId = order;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Coupon)) {
            return false;
        }
        return id != null && id.equals(((Coupon) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Coupon{" +
            "id=" + getId() +
            ", couponId='" + getCouponId() + "'" +
            ", offRate=" + getOffRate() +
            "}";
    }
}
