package com.squarecross.photoalbum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long tokenExpiresIn;

    public TokenDto(String accessTkn, String refreshTkn) {
        this.accessToken = accessTkn;
        this.refreshToken = refreshTkn;
    }

    public TokenDto clearRefreshToken() {
        this.refreshToken = "";
        return this;
    }
}
