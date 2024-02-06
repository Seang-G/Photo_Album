package com.squarecross.photoalbum.config;

import com.squarecross.photoalbum.domain.Member;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Objects;

public class SecurityUtil {
    public static Long getLoginUserId(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return Long.valueOf(authentication.getName());
    }

}
