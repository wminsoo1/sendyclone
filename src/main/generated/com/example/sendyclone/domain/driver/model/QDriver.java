package com.example.sendyclone.domain.driver.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDriver is a Querydsl query type for Driver
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDriver extends EntityPathBase<Driver> {

    private static final long serialVersionUID = -819231622L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDriver driver = new QDriver("driver");

    public final QDriverAddress driverAddress;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath password = createString("password");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final QVehicle vehicle;

    public QDriver(String variable) {
        this(Driver.class, forVariable(variable), INITS);
    }

    public QDriver(Path<? extends Driver> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDriver(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDriver(PathMetadata metadata, PathInits inits) {
        this(Driver.class, metadata, inits);
    }

    public QDriver(Class<? extends Driver> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.driverAddress = inits.isInitialized("driverAddress") ? new QDriverAddress(forProperty("driverAddress")) : null;
        this.vehicle = inits.isInitialized("vehicle") ? new QVehicle(forProperty("vehicle")) : null;
    }

}

