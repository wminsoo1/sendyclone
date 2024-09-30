package com.example.sendyclone.domain.wallet.repository;

import com.example.sendyclone.domain.wallet.model.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
