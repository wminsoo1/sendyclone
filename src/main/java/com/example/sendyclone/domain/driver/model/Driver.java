package com.example.sendyclone.domain.driver.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    @Size(min = 8, max = 32)
    private String password;

    @NotNull
    private String phoneNumber;

    @Embedded
    private DriverAddress driverAddress;

    @Embedded
    private Vehicle vehicle;

    private Driver(String name, String password, String phoneNumber, DriverAddress driverAddress, Vehicle vehicle) {
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.driverAddress = driverAddress;
        this.vehicle = vehicle;
    }
}

