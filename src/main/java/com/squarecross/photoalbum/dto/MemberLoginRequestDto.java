package com.squarecross.photoalbum.dto;

import com.squarecross.photoalbum.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class MemberLoginRequestDto {
    private String username;
    private String password;
    private String nickname;

    public MemberLoginRequestDto of(Member member) {
        return MemberLoginRequestDto.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .build();
    }

}