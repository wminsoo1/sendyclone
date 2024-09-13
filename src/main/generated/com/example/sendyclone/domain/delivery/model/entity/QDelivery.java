package com.example.sendyclone.domain.delivery.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDelivery is a Querydsl query type for Delivery
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDelivery extends EntityPathBase<Delivery> {

    private static final long serialVersionUID = 1494586969L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDelivery delivery = new QDelivery("delivery");

    public final com.example.sendyclone.domain.delivery.model.QDeliveryAddress deliveryAddress;

    public final com.example.sendyclone.domain.delivery.model.QDeliveryCategory deliveryCategory;

    public final DateTimePath<java.time.LocalDateTime> deliveryDate = createDateTime("deliveryDate", java.time.LocalDateTime.class);

    public final NumberPath<Double> deliveryFee = createNumber("deliveryFee", Double.class);

    public final StringPath deliveryOptions = createString("deliveryOptions");

    public final EnumPath<com.example.sendyclone.domain.delivery.model.DeliveryStatus> deliveryStatus = createEnum("deliveryStatus", com.example.sendyclone.domain.delivery.model.DeliveryStatus.class);

    public final com.example.sendyclone.domain.driver.model.QDriver driver;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.example.sendyclone.domain.member.model.entity.QMember member;

    public final StringPath reservationNumber = createString("reservationNumber");

    public final com.example.sendyclone.domain.driver.model.QVehicle vehicle;

    public QDelivery(String variable) {
        this(Delivery.class, forVariable(variable), INITS);
    }

    public QDelivery(Path<? extends Delivery> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDelivery(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDelivery(PathMetadata metadata, PathInits inits) {
        this(Delivery.class, metadata, inits);
    }

    public QDelivery(Class<? extends Delivery> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.deliveryAddress = inits.isInitialized("deliveryAddress") ? new com.example.sendyclone.domain.delivery.model.QDeliveryAddress(forProperty("deliveryAddress")) : null;
        this.deliveryCategory = inits.isInitialized("deliveryCategory") ? new com.example.sendyclone.domain.delivery.model.QDeliveryCategory(forProperty("deliveryCategory")) : null;
        this.driver = inits.isInitialized("driver") ? new com.example.sendyclone.domain.driver.model.QDriver(forProperty("driver"), inits.get("driver")) : null;
        this.member = inits.isInitialized("member") ? new com.example.sendyclone.domain.member.model.entity.QMember(forProperty("member")) : null;
        this.vehicle = inits.isInitialized("vehicle") ? new com.example.sendyclone.domain.driver.model.QVehicle(forProperty("vehicle")) : null;
    }

}

