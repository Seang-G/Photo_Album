package com.squarecross.photoalbum.mapper;

import com.squarecross.photoalbum.domain.Authority;
import com.squarecross.photoalbum.domain.Member;
import com.squarecross.photoalbum.dto.MemberLoginRequestDto;
import com.squarecross.photoalbum.dto.MemberSignUpRequestDto;
import lombok.Builder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class MemberMapper {
    @Builder
    public static Member convertToModel(MemberSignUpRequestDto memberDto){
        return Member.builder()
                .username(memberDto.getUsername())
                .nickname(memberDto.getNickname())
                .password(memberDto.getPassword())
                .authority(Authority.ROLE_USER)
                .build();
    }

    public static UsernamePasswordAuthenticationToken toAuthentication(MemberLoginRequestDto memberDto) {
        return new UsernamePasswordAuthenticationToken(memberDto.getUsername(), memberDto.getPassword());
    }
}
