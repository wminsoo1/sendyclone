package com.example.sendyclone.domain.member.service;

import com.example.sendyclone.domain.member.exception.MemberErrorCode;
import com.example.sendyclone.domain.member.exception.MemberException;
import com.example.sendyclone.domain.member.model.entity.Member;
import com.example.sendyclone.domain.member.model.request.MemberSaveRequest;
import com.example.sendyclone.domain.member.model.response.MemberSaveResponse;
import com.example.sendyclone.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import static com.example.sendyclone.domain.member.exception.MemberErrorCode.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberSaveResponse saveMember(MemberSaveRequest memberSaveRequest) {
        validateDuplicateEmail(memberSaveRequest.getEmail());

        final Member member = memberSaveRequest.toMember();

        Member savedMember;
        try {
            savedMember = memberRepository.save(member);
        } catch (DataIntegrityViolationException e) {
            throw new MemberException(DUPLICATE_PASSWORD);
        }

        return MemberSaveResponse.fromMember(savedMember);
    }

    private void validateDuplicateEmail(String email) {
        final boolean isDuplicate = memberRepository.findMemberByEmail(email).isPresent();
        if (isDuplicate) {
            throw MemberException.fromErrorCode(DUPLICATE_PASSWORD);
        }
    }
}
