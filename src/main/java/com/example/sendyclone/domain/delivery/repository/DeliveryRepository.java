package com.example.sendyclone.domain.delivery.repository;

import com.example.sendyclone.domain.delivery.model.DeliveryStatus;
import com.example.sendyclone.domain.delivery.model.entity.Delivery;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from Delivery d where d.id = :deliveryId")
    Optional<Delivery> findByIdWithPLock(Long deliveryId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from Delivery d where d.reservationNumber = :reservationNumber")
    Optional<Delivery> findByReservationNumberWithPLock(@Param("reservationNumber") String reservationNumber);

    Optional<Delivery> findByReservationNumber(@Param("reservationNumber") String reservationNumber);

    @Query("select d from Delivery d join fetch d.member m where m.id = :memberId and d.deliveryStatus = :deliveryStatus")
    List<Delivery> findCompletedDeliveriesByMemberId(@Param("memberId") Long memberId, @Param("deliveryStatus") DeliveryStatus deliveryStatus);

    @Query("select d from Delivery d join fetch d.member m where m.id = :memberId and d.deliveryStatus != :deliveryStatus")
    List<Delivery> findNotCompletedDeliveriesByMemberId(@Param("memberId") Long memberId, @Param("deliveryStatus") DeliveryStatus deliveryStatus);

    @Query("select d from Delivery d join fetch d.member m where m.id = :memberId and d.deliveryStatus in :deliveryStatuses")
    List<Delivery> findNotCompletedDeliveriesByMemberId(@Param("memberId") Long memberId, @Param("deliveryStatuses") List<DeliveryStatus> deliveryStatuses);
}
