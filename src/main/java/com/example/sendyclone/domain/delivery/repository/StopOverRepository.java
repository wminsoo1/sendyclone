package com.example.sendyclone.domain.delivery.repository;

import com.example.sendyclone.domain.delivery.model.entity.StopOver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StopOverRepository extends JpaRepository<StopOver, Long> {

    @Query("select s from StopOver s join fetch s.delivery d where d.id = :deliveryId")
    List<StopOver> findStopOverByDeliveryId(@Param("deliveryId") Long deliveryId);

    @Query("select s from StopOver s join fetch s.delivery d where d.id IN :deliveryIds")
    List<StopOver> findStopOverByDeliveryIds(@Param("deliveryIds") List<Long> deliveryIds);
}
