package com.example.sendyclone.domain.wallet.service;

import com.example.sendyclone.domain.wallet.model.entity.Wallet;
import com.example.sendyclone.domain.wallet.model.request.CreateWalletRequest;
import com.example.sendyclone.domain.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    public void createWallet(CreateWalletRequest createWalletRequest) {
        Wallet wallet = CreateWalletRequest.to(createWalletRequest);
    }
}
