package com.squarecross.photoalbum.repository;

import com.squarecross.photoalbum.domain.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    int countByAlbum_AlbumId(Long AlbumId);
    int countByAlbum_AlbumName(String AlbumName);

    List<Photo> findTop4ByAlbum_AlbumIdOrderByUploadedAtDesc(Long AlbumId);

    Optional<Photo> findByFileNameAndAlbum_AlbumId(String photoName, Long albumId);

    List<Photo> findByAlbum_AlbumIdAndFileNameContainingOrderByUploadedAtAsc(Long AlbumId, String FileName);
    List<Photo> findByAlbum_AlbumIdAndFileNameContainingOrderByUploadedAtDesc(Long AlbumId, String FileName);
    List<Photo> findByAlbum_AlbumIdAndFileNameContainingOrderByFileNameAsc(Long AlbumId, String FileName);
    List<Photo> findByAlbum_AlbumIdAndFileNameContainingOrderByFileNameDesc(Long AlbumId, String FileName);
}
