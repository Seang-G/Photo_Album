package com.squarecross.photoalbum.repository;

import com.squarecross.photoalbum.domain.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    Optional<Album> findByAlbumName(String albumName);
    List<Album> findByMemberIdAndAlbumNameContainingOrderByCreatedAtAsc(Long memberId, String albumName);
    List<Album> findByMemberIdAndAlbumNameContainingOrderByCreatedAtDesc(Long memberId, String albumName);
    List<Album> findByMemberIdAndAlbumNameContainingOrderByAlbumNameAsc(Long memberId, String albumName);
    List<Album> findByMemberIdAndAlbumNameContainingOrderByAlbumNameDesc(Long memberId, String albumName);

}
