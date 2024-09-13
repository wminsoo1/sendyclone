package com.example.sendyclone.domain.delivery.repository;

import com.example.sendyclone.domain.delivery.model.entity.Delivery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long>, DeliveryRepositoryCustom {

    @Query("select d from Delivery d join fetch d.member m where d.reservationNumber = :reservationNumber and m.id = :memberId")
    Optional<Delivery> findDeliveryByReservationNumberAndMemberId(@Param("reservationNumber") String reservationNumber, @Param("memberId") Long memberId);

    @Query("select d from Delivery d join fetch d.member m where m.id = :memberId")
    List<Delivery> findDeliveriesByMemberId(@Param("memberId") Long memberId);

    boolean existsByReservationNumber(String reservationNumber);
}
