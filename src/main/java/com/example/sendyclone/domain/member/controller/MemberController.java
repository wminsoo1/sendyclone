package com.example.sendyclone.domain.member.controller;

import com.example.sendyclone.domain.member.model.request.MemberSaveRequest;
import com.example.sendyclone.domain.member.model.response.MemberSaveResponse;
import com.example.sendyclone.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/member")
    public ResponseEntity<MemberSaveResponse> memberSave(@Valid @RequestBody MemberSaveRequest memberSaveRequest) {
        final MemberSaveResponse memberSaveResponse = memberService.saveMember(memberSaveRequest);

        return ResponseEntity.ok().body(memberSaveResponse);
    }
}
