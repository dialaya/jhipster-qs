package com.dialaya.jh.repository.rowmapper;

import com.dialaya.jh.domain.Membership;
import com.dialaya.jh.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Membership}, with proper type conversions.
 */
@Service
public class MembershipRowMapper implements BiFunction<Row, String, Membership> {

    private final ColumnConverter converter;

    public MembershipRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Membership} stored in the database.
     */
    @Override
    public Membership apply(Row row, String prefix) {
        Membership entity = new Membership();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setMemberEmail(converter.fromRow(row, prefix + "_member_email", String.class));
        entity.setOrganisationName(converter.fromRow(row, prefix + "_organisation_name", String.class));
        entity.setOrganizationId(converter.fromRow(row, prefix + "_organization_id", Long.class));
        entity.setMemberId(converter.fromRow(row, prefix + "_member_id", Long.class));
        return entity;
    }
}
