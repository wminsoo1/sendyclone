package com.example.sendyclone.domain.wallet.model.request;

import com.example.sendyclone.domain.wallet.model.entity.Wallet;
import lombok.Getter;

@Getter
public class CreateWalletRequest {

    private Long memberId;

    public static Wallet to(CreateWalletRequest createWalletRequest) {
        return Wallet.builder()
                .id(createWalletRequest.getMemberId())
                .build();
    }
}
