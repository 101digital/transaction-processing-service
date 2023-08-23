package io.marketplace.services.transaction.processing.specification;

import io.marketplace.services.transaction.processing.dto.RequestSearchConfiguration;
import io.marketplace.services.transaction.processing.entity.ConfigurationEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class ConfigurationsSpecification implements Specification<ConfigurationEntity> {

    private static final long serialVersionUID = 5026086831550106678L;

    private static final String ORDER_BY_CREATED_AT_ASC = "createdAt-ASC";
    private static final String ORDER_BY_CREATED_AT_DESC = "createdAt-DESC";
    private static final String ORDER_BY_UPDATED_AT_ASC = "updatedAt-ASC";
    private static final String ORDER_BY_UPDATED_AT_DESC = "updatedAt-DESC";
    private static final String TYPE = "type";
    private static final String WALLET_ID = "wallet";
    private static final String CREATED_AT = "createdAt";
    private static final String UPDATED_AT = "updatedAt";

    private final transient RequestSearchConfiguration requestSearchConfiguration;

    public ConfigurationsSpecification(RequestSearchConfiguration requestSearchConfiguration) {
        super();
        this.requestSearchConfiguration = requestSearchConfiguration;
    }

    @Override
    public Predicate toPredicate(
            Root<ConfigurationEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.isNotEmpty(requestSearchConfiguration.getType())) {
            predicates.add(cb.equal(root.get(TYPE), requestSearchConfiguration.getType()));
        }

        if (StringUtils.isNotEmpty(requestSearchConfiguration.getWalletId())) {
            predicates.add(cb.equal(root.get(WALLET_ID), requestSearchConfiguration.getWalletId()));
        }

        List<Order> orders = new ArrayList<>();
        if (io.marketplace.commons.utils.StringUtils.isNotEmpty(
                requestSearchConfiguration.getListOrders())) {
            String[] orderFields = requestSearchConfiguration.getListOrders().split(",");
            for (String orderField : orderFields) {
                orderField = orderField.trim();
                if (ORDER_BY_CREATED_AT_DESC.equalsIgnoreCase(orderField)) {
                    orders.add(cb.desc(root.get(CREATED_AT)));
                } else if (ORDER_BY_CREATED_AT_ASC.equalsIgnoreCase(orderField)) {
                    orders.add(cb.asc(root.get(CREATED_AT)));
                }

                if (ORDER_BY_UPDATED_AT_DESC.equalsIgnoreCase(orderField)) {
                    orders.add(cb.desc(root.get(UPDATED_AT)));
                } else if (ORDER_BY_UPDATED_AT_ASC.equalsIgnoreCase(orderField)) {
                    orders.add(cb.asc(root.get(UPDATED_AT)));
                }
            }
        }

        if (orders.isEmpty()) {
            orders.add(cb.desc(root.get(CREATED_AT)));
        }

        return query.where(cb.and(predicates.toArray(new Predicate[0])))
                .distinct(true)
                .orderBy(orders)
                .getRestriction();
    }
}
