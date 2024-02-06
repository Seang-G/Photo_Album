package com.squarecross.photoalbum.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="refresh_token", schema="photo_album", uniqueConstraints = {@UniqueConstraint(columnNames = "refresh_token_id")})
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id", unique = true, nullable = false)
    private Long refreshTokenId;

    @Column(name="refresh_token", unique = false, nullable = true)
    private String refreshToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    public RefreshToken(String token, Member member) {
        this.refreshToken = token;
        this.member = member;
    }

    public RefreshToken updateToken(String token) {
        this.refreshToken = token;
        return this;
    }

}
