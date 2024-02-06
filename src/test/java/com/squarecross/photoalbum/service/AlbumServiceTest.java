package com.squarecross.photoalbum.service;

import com.squarecross.photoalbum.Constants;
import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.domain.Photo;
import com.squarecross.photoalbum.dto.AlbumDto;
import com.squarecross.photoalbum.repository.AlbumRepository;
import com.squarecross.photoalbum.repository.PhotoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AlbumServiceTest {

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    AlbumService albumService;

    @Test
    void testGetAlbum() {
//        Album album = new Album();
//        album.setAlbumName("테스트");
//        Album savedAlbum = albumRepository.save(album);
//
//        AlbumDto resAlbum = albumService.getAlbum(savedAlbum.getAlbumId());
//        assertEquals("테스트", resAlbum.getAlbumName());
//
//        resAlbum = albumService.getAlbum(savedAlbum.getAlbumName());
//        assertEquals("테스트", resAlbum.getAlbumName());
    }

    @Test
    void testException() {
//        try {
//            albumService.getAlbum(11239L);
//        } catch (EntityNotFoundException e){
//            System.out.println(e);
//        }
//
//        try {
//            albumService.getAlbum("없는앨범이름");
//        } catch (EntityNotFoundException e){
//            System.out.println(e);
//        }
    }

    @Test
    void testCountPhoto() {
//        Album album = new Album();
//        album.setAlbumName("테스트");
//        Album savedAlbum = albumRepository.save(album);
//
//        for(int i=0; i<8; i++) {
//            Photo photo = new Photo();
//            photo.setFileName(String.format("photo %d", i));
//            photo.setAlbum(savedAlbum);
//            photoRepository.save(photo);
//        }
//
//        AlbumDto resAlbum = albumService.getAlbum(savedAlbum.getAlbumId());
//        assertEquals(8, resAlbum.getCount());
//
//        resAlbum = albumService.getAlbum(savedAlbum.getAlbumName());
//        assertEquals(8, resAlbum.getCount());

    }

    @Test
    void testAlbumCreate() throws IOException {
        AlbumDto albumDto = new AlbumDto();
        albumDto.setAlbumName("테스트 앨범");
        AlbumDto savedAlbum = albumService.createAlbum(albumDto);

        assertEquals("테스트 앨범", savedAlbum.getAlbumName());

        albumService.deleteAlbum(savedAlbum.getAlbumId());

        Throwable exception = assertThrows(EntityNotFoundException.class, () -> albumService.getAlbum(savedAlbum.getAlbumId()));
        assertEquals(String.format("앨범 아이디 %d로 조회되지 않았습니다,", savedAlbum.getAlbumId()), exception.getMessage());
    }

//    @Test
//    void testAlbumRepository() throws InterruptedException {
//        Album album1 = new Album();
//        Album album2 = new Album();
//        album1.setAlbumName("aaaa");
//        album2.setAlbumName("aaab");
//
//        albumRepository.save(album1);
//        TimeUnit.SECONDS.sleep(1);
//        albumRepository.save(album2);
//
//        List<Album> resDate = albumRepository.findByAlbumNameAndMemberIdContainingOrderByCreatedAtDesc("aaa", album1.getAlbumId());
//
//        assertEquals("aaab", resDate.get(0).getAlbumName()); // 0번째 Index가 두번째 앨범명 aaab 인지 체크
//        assertEquals("aaaa", resDate.get(1).getAlbumName()); // 1번째 Index가 첫번째 앨범명 aaaa 인지 체크
//        assertEquals(2, resDate.size()); // aaa 이름을 가진 다른 앨범이 없다는 가정하에, 검색 키워드에 해당하는 앨범 필터링 체크
//
//        //앨범명 정렬, aaaa -> aaab 기준으로 나와야합니다
//        List<Album> resName = albumRepository.findByAlbumNameAndMemberIdContainingOrderByAlbumNameAsc("aaa", album1.getAlbumId());
//
//        assertEquals("aaaa", resName.get(0).getAlbumName()); // 0번째 Index가 두번째 앨범명 aaaa 인지 체크
//        assertEquals("aaab", resName.get(1).getAlbumName()); // 1번째 Index가 두번째 앨범명 aaab 인지 체크
//        assertEquals(2, resName.size()); // aaa 이름을 가진 다른 앨범이 없다는 가정하에, 검색 키워드에 해당하는 앨범 필터링 체크
//    }

    @Test
    void testChangeAlbumName() throws IOException {
        AlbumDto albumDto = new AlbumDto();
        albumDto.setAlbumName("변경전");
        AlbumDto res = albumService.createAlbum(albumDto);

        Long albumId = res.getAlbumId();
        AlbumDto updateDto = new AlbumDto();
        updateDto.setAlbumName("변경후");
        albumService.changeAlbumName(albumId, updateDto);

        AlbumDto updatedDto = albumService.getAlbum(albumId);

        assertEquals("변경후", updatedDto.getAlbumName());
    }

}