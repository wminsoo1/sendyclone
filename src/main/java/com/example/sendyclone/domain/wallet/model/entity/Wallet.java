package com.example.sendyclone.domain.wallet.model.entity;

import com.example.sendyclone.domain.member.model.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wallet {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private BigDecimal balance;

    public void updateMember(Member member) {
        this.member = member;
    }

    private Wallet(Long id, Member member, BigDecimal balance) {
        this.id = id;
        this.member = member;
        this.balance = balance;
    }
}
