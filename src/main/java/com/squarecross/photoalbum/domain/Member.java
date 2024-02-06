package com.squarecross.photoalbum.domain;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class Member extends BaseTimeEntity{

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 45, unique = true)
    private String username;

    @Column(length = 45)
    private  String nickname;

    @Column(length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member", cascade = CascadeType.ALL)
    private List<Album> albums;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "member", cascade = CascadeType.ALL)
    private RefreshToken refreshToken;

    public void encodePassword(PasswordEncoder passwordEncoder){ this.password = passwordEncoder.encode(password); }

    public boolean checkPassword(PasswordEncoder passwordEncoder, String password) {
        return passwordEncoder.matches(password, this.password);
    }

    @Builder
    public Member(Long id, String username, String password, String nickname, Authority authority) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.authority = authority;
    }
}
