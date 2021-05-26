package com.dialaya.jh.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.dialaya.jh.domain.Membership;
import com.dialaya.jh.repository.rowmapper.MemberRowMapper;
import com.dialaya.jh.repository.rowmapper.MembershipRowMapper;
import com.dialaya.jh.repository.rowmapper.OrganizationRowMapper;
import com.dialaya.jh.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
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
 * Spring Data SQL reactive custom repository implementation for the Membership entity.
 */
@SuppressWarnings("unused")
class MembershipRepositoryInternalImpl implements MembershipRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final OrganizationRowMapper organizationMapper;
    private final MemberRowMapper memberMapper;
    private final MembershipRowMapper membershipMapper;

    private static final Table entityTable = Table.aliased("membership", EntityManager.ENTITY_ALIAS);
    private static final Table organizationTable = Table.aliased("organization", "e_organization");
    private static final Table memberTable = Table.aliased("member", "e_member");

    public MembershipRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        OrganizationRowMapper organizationMapper,
        MemberRowMapper memberMapper,
        MembershipRowMapper membershipMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.organizationMapper = organizationMapper;
        this.memberMapper = memberMapper;
        this.membershipMapper = membershipMapper;
    }

    @Override
    public Flux<Membership> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Membership> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Membership> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = MembershipSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(OrganizationSqlHelper.getColumns(organizationTable, "organization"));
        columns.addAll(MemberSqlHelper.getColumns(memberTable, "member"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(organizationTable)
            .on(Column.create("organization_id", entityTable))
            .equals(Column.create("id", organizationTable))
            .leftOuterJoin(memberTable)
            .on(Column.create("member_id", entityTable))
            .equals(Column.create("id", memberTable));

        String select = entityManager.createSelect(selectFrom, Membership.class, pageable, criteria);
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
    public Flux<Membership> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Membership> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Membership process(Row row, RowMetadata metadata) {
        Membership entity = membershipMapper.apply(row, "e");
        entity.setOrganization(organizationMapper.apply(row, "organization"));
        entity.setMember(memberMapper.apply(row, "member"));
        return entity;
    }

    @Override
    public <S extends Membership> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Membership> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update Membership with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(Membership entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class MembershipSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("member_email", table, columnPrefix + "_member_email"));
        columns.add(Column.aliased("organisation_name", table, columnPrefix + "_organisation_name"));

        columns.add(Column.aliased("organization_id", table, columnPrefix + "_organization_id"));
        columns.add(Column.aliased("member_id", table, columnPrefix + "_member_id"));
        return columns;
    }
}
