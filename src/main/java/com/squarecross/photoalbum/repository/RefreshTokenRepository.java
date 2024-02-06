package com.squarecross.photoalbum.repository;

import com.squarecross.photoalbum.domain.Member;
import com.squarecross.photoalbum.domain.Photo;
import com.squarecross.photoalbum.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByMember(Member member);
}
