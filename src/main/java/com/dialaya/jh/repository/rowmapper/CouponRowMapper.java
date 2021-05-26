package com.dialaya.jh.repository.rowmapper;

import com.dialaya.jh.domain.Coupon;
import com.dialaya.jh.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Coupon}, with proper type conversions.
 */
@Service
public class CouponRowMapper implements BiFunction<Row, String, Coupon> {

    private final ColumnConverter converter;

    public CouponRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Coupon} stored in the database.
     */
    @Override
    public Coupon apply(Row row, String prefix) {
        Coupon entity = new Coupon();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCouponId(converter.fromRow(row, prefix + "_coupon_id", String.class));
        entity.setOffRate(converter.fromRow(row, prefix + "_off_rate", Long.class));
        entity.setOrderId(converter.fromRow(row, prefix + "_order_id", Long.class));
        return entity;
    }
}
