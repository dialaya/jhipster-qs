package com.dialaya.jh.repository.rowmapper;

import com.dialaya.jh.domain.Member;
import com.dialaya.jh.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Member}, with proper type conversions.
 */
@Service
public class MemberRowMapper implements BiFunction<Row, String, Member> {

    private final ColumnConverter converter;

    public MemberRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Member} stored in the database.
     */
    @Override
    public Member apply(Row row, String prefix) {
        Member entity = new Member();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setPhoneNumber(converter.fromRow(row, prefix + "_phone_number", String.class));
        entity.setRequestDate(converter.fromRow(row, prefix + "_request_date", Instant.class));
        entity.setLocationId(converter.fromRow(row, prefix + "_location_id", Long.class));
        entity.setLegalRepresentativeId(converter.fromRow(row, prefix + "_legal_representative_id", Long.class));
        return entity;
    }
}
