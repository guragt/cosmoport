package com.space.service;

import com.space.model.Ship;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;

public class ShipSpecification implements Specification<Ship> {

    private SearchCriteria criteria;

    public ShipSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        if (criteria.getOperation().equalsIgnoreCase(">")){
            if(root.get(criteria.getKey()).getJavaType() == Date.class){
                return builder.greaterThanOrEqualTo(
                        root.<Date>get(criteria.getKey()), (Date)criteria.getValue());
            } else {
                return builder.greaterThanOrEqualTo(
                        root.<String>get(criteria.getKey()), criteria.getValue().toString());
            }
        } else if (criteria.getOperation().equalsIgnoreCase("<")){
            if(root.get(criteria.getKey()).getJavaType() == Date.class) {
                return builder.lessThanOrEqualTo(
                        root.<Date>get(criteria.getKey()), (Date)criteria.getValue());
            } else {
                return builder.lessThanOrEqualTo(
                        root.<String>get(criteria.getKey()), criteria.getValue().toString());
            }
        } else if (criteria.getOperation().equalsIgnoreCase(":")){
            if(root.get(criteria.getKey()).getJavaType() == String.class){
                return builder.like(
                        root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
            } else {
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        }
        return null;
    }
}
