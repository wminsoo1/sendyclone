package com.example.sendyclone.domain.member.service;

import com.example.sendyclone.domain.member.exception.DuplicatePasswordException;
import com.example.sendyclone.domain.member.model.entity.Member;
import com.example.sendyclone.domain.member.model.request.MemberSaveRequest;
import com.example.sendyclone.domain.member.model.response.MemberSaveResponse;
import com.example.sendyclone.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberSaveResponse saveMember(MemberSaveRequest memberSaveRequest) {
        validateDuplicateEmail(memberSaveRequest.getEmail());

        final Member member = memberSaveRequest.toMember();

        final Member savedMember = memberRepository.save(member);

        return MemberSaveResponse.fromMember(savedMember);
    }

    private void validateDuplicateEmail(String email) {
        final boolean isDuplicate = memberRepository.findMemberByEmail(email).isPresent();
        if (isDuplicate) {
            throw new DuplicatePasswordException();
        }
    }
}
