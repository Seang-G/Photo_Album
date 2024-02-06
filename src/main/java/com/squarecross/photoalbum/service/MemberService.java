package com.squarecross.photoalbum.service;

import antlr.Token;
import com.squarecross.photoalbum.auth.JwtTokenProvider;
import com.squarecross.photoalbum.config.SecurityUtil;
import com.squarecross.photoalbum.domain.Member;
import com.squarecross.photoalbum.domain.RefreshToken;
import com.squarecross.photoalbum.dto.MemberLoginRequestDto;
import com.squarecross.photoalbum.dto.MemberSignUpRequestDto;
import com.squarecross.photoalbum.dto.MemberResponseDto;
import com.squarecross.photoalbum.dto.TokenDto;
import com.squarecross.photoalbum.mapper.MemberMapper;
import com.squarecross.photoalbum.repository.MemberRepository;
import com.squarecross.photoalbum.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    @Autowired
    private final MemberRepository memberRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder managerBuilder;

    @Autowired
    private final RefreshTokenRepository refreshTokenRepository;


    @Transactional
    public Long signUp(MemberSignUpRequestDto requestDto) throws RuntimeException{
        if (memberRepository.findByUsername(requestDto.getUsername()).isPresent())
            throw new RuntimeException("이미 존재하는 이메일입니다.");

        if (!Objects.equals(requestDto.getPassword(), requestDto.getCheckedPassword()))
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");


        Member savedMember = memberRepository.save(MemberMapper.convertToModel(requestDto));
        savedMember.encodePassword(passwordEncoder);

        return savedMember.getId();
    }


    @Transactional(readOnly = false)
    public TokenDto login(MemberLoginRequestDto requestDto) {
        Optional<Member> memberOpt =  memberRepository.findByUsername(requestDto.getUsername());

        if (memberOpt.isEmpty()) throw new EntityNotFoundException("등록되지 않은 이메일입니다.");
        Member member = memberOpt.get();

        boolean isPasswordRight =  member.checkPassword(passwordEncoder, requestDto.getPassword());
        if (!isPasswordRight) throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");

        UsernamePasswordAuthenticationToken token = MemberMapper.toAuthentication(requestDto);
        Authentication authentication = managerBuilder.getObject().authenticate(token);

        TokenDto tokenDto = jwtTokenProvider.generateTokenDto(authentication);
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByMember(member);

        if(refreshTokenOpt.isEmpty()) {
            RefreshToken refreshToken = new RefreshToken(tokenDto.getRefreshToken(), member);
            refreshTokenRepository.save(refreshToken);
        } else {
            refreshTokenOpt.get().updateToken(tokenDto.getRefreshToken());
        }

        return tokenDto;
    }

    public String getNickname() {
        Long memberId = SecurityUtil.getLoginUserId();
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) throw new UsernameNotFoundException("자격증명에 실패하였습니다.");
        Member member = memberOpt.get();

        return member.getNickname();
    }

    public TokenDto refreshAccessToken(String token) {
        if(!jwtTokenProvider.validateRefreshToken(token)) throw new IllegalArgumentException("검증에 실패하였습니다.");
        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        TokenDto tokenDto = jwtTokenProvider.generateTokenDto(authentication);
        return tokenDto.clearRefreshToken();
    }
}
