package com.example.sendyclone.domain.driver.repository;

import com.example.sendyclone.domain.driver.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Long> {
}
