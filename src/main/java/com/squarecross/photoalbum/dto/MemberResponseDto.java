package com.squarecross.photoalbum.dto;

import com.squarecross.photoalbum.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
public class MemberResponseDto {
    private String username;
    private String nickname;

    public static MemberResponseDto of(Member member) {
        return MemberResponseDto.builder()
                .username(member.getUsername())
                .nickname(member.getNickname())
                .build();
    }
}
