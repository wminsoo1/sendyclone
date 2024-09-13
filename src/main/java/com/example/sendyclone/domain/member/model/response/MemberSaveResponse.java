package com.example.sendyclone.domain.member.model.response;

import com.example.sendyclone.domain.member.model.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MemberSaveResponse {

    private String name;

    private String email;

    private MemberSaveResponse() {
    }

    private MemberSaveResponse(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public static MemberSaveResponse fromMember(final Member member) {
        return new MemberSaveResponse(member.getName(), member.getEmail());
    }
}
