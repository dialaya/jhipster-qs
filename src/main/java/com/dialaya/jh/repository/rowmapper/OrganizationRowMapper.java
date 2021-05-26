package com.dialaya.jh.repository.rowmapper;

import com.dialaya.jh.domain.Organization;
import com.dialaya.jh.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Organization}, with proper type conversions.
 */
@Service
public class OrganizationRowMapper implements BiFunction<Row, String, Organization> {

    private final ColumnConverter converter;

    public OrganizationRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Organization} stored in the database.
     */
    @Override
    public Organization apply(Row row, String prefix) {
        Organization entity = new Organization();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setBaseRate(converter.fromRow(row, prefix + "_base_rate", Long.class));
        entity.setGroupRate(converter.fromRow(row, prefix + "_group_rate", Long.class));
        entity.setFullRate(converter.fromRow(row, prefix + "_full_rate", Long.class));
        entity.setLocationId(converter.fromRow(row, prefix + "_location_id", Long.class));
        return entity;
    }
}
