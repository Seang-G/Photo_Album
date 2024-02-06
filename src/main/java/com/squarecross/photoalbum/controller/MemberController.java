package com.squarecross.photoalbum.controller;

import antlr.Token;
import com.squarecross.photoalbum.dto.MemberLoginRequestDto;
import com.squarecross.photoalbum.dto.MemberSignUpRequestDto;
import com.squarecross.photoalbum.dto.TokenDto;
import com.squarecross.photoalbum.repository.MemberRepository;
import com.squarecross.photoalbum.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @RequestMapping(value = "/join", method = RequestMethod.POST)
    public ResponseEntity<Long> join(@Valid @RequestBody final MemberSignUpRequestDto requestDto){
        Long id = memberService.signUp(requestDto);

        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<TokenDto> login(@RequestBody final MemberLoginRequestDto memberLoginRequestDto) {
        TokenDto token = memberService.login(memberLoginRequestDto);

        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<String> getNickname() {
        String nickname = memberService.getNickname();
        return new ResponseEntity<>(nickname, HttpStatus.OK);
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    public ResponseEntity<TokenDto> refreshAccessToken(@RequestParam final String refreshToken) {
        TokenDto tokenDto = memberService.refreshAccessToken(refreshToken);
        return new ResponseEntity<>(tokenDto, HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<String> errorHandling(Exception e) {

        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> validErrorHandling(MethodArgumentNotValidException e) {
        return new ResponseEntity<>(e.getBindingResult().getAllErrors().get(0).getDefaultMessage(), HttpStatus.BAD_REQUEST);
    }
}
