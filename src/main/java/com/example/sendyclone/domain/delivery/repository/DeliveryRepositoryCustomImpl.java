package com.example.sendyclone.domain.delivery.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;

public class DeliveryRepositoryCustomImpl implements DeliveryRepositoryCustom{

    private JPAQueryFactory queryFactory;

    @Autowired
    public DeliveryRepositoryCustomImpl(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }



}
