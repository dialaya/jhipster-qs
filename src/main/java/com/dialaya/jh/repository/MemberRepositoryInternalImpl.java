package com.dialaya.jh.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.dialaya.jh.domain.Member;
import com.dialaya.jh.repository.rowmapper.LocationRowMapper;
import com.dialaya.jh.repository.rowmapper.MemberRowMapper;
import com.dialaya.jh.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Member entity.
 */
@SuppressWarnings("unused")
class MemberRepositoryInternalImpl implements MemberRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final LocationRowMapper locationMapper;
    private final MemberRowMapper memberMapper;

    private static final Table entityTable = Table.aliased("member", EntityManager.ENTITY_ALIAS);
    private static final Table locationTable = Table.aliased("location", "location");
    private static final Table legalRepresentativeTable = Table.aliased("member", "legalRepresentative");

    public MemberRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        LocationRowMapper locationMapper,
        MemberRowMapper memberMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.locationMapper = locationMapper;
        this.memberMapper = memberMapper;
    }

    @Override
    public Flux<Member> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Member> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Member> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = MemberSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(LocationSqlHelper.getColumns(locationTable, "location"));
        columns.addAll(MemberSqlHelper.getColumns(legalRepresentativeTable, "legalRepresentative"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(locationTable)
            .on(Column.create("location_id", entityTable))
            .equals(Column.create("id", locationTable))
            .leftOuterJoin(legalRepresentativeTable)
            .on(Column.create("legal_representative_id", entityTable))
            .equals(Column.create("id", legalRepresentativeTable));

        String select = entityManager.createSelect(selectFrom, Member.class, pageable, criteria);
        String alias = entityTable.getReferenceName().getReference();
        String selectWhere = Optional
            .ofNullable(criteria)
            .map(
                crit ->
                    new StringBuilder(select)
                        .append(" ")
                        .append("WHERE")
                        .append(" ")
                        .append(alias)
                        .append(".")
                        .append(crit.toString())
                        .toString()
            )
            .orElse(select); // TODO remove once https://github.com/spring-projects/spring-data-jdbc/issues/907 will be fixed
        return db.sql(selectWhere).map(this::process);
    }

    @Override
    public Flux<Member> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Member> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Member process(Row row, RowMetadata metadata) {
        Member entity = memberMapper.apply(row, "e");
        entity.setLocation(locationMapper.apply(row, "location"));
        entity.setLegalRepresentative(memberMapper.apply(row, "legalRepresentative"));
        return entity;
    }

    @Override
    public <S extends Member> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Member> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update Member with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(Member entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class MemberSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("title", table, columnPrefix + "_title"));
        columns.add(Column.aliased("first_name", table, columnPrefix + "_first_name"));
        columns.add(Column.aliased("last_name", table, columnPrefix + "_last_name"));
        columns.add(Column.aliased("email", table, columnPrefix + "_email"));
        columns.add(Column.aliased("phone_number", table, columnPrefix + "_phone_number"));
        columns.add(Column.aliased("request_date", table, columnPrefix + "_request_date"));

        columns.add(Column.aliased("location_id", table, columnPrefix + "_location_id"));
        columns.add(Column.aliased("legal_representative_id", table, columnPrefix + "_legal_representative_id"));
        return columns;
    }
}
