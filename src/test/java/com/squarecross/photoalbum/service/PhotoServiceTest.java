//package com.squarecross.photoalbum.service;
//
//import com.squarecross.photoalbum.domain.Photo;
//import com.squarecross.photoalbum.dto.PhotoDto;
//import com.squarecross.photoalbum.repository.PhotoRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@Transactional
//class PhotoServiceTest {
//
//    @Autowired
//    PhotoRepository photoRepository;
//
//    @Autowired
//    PhotoService photoService;
//    @Test
//    void testGetPhoto() IO{
//        Photo photo = new Photo();
//        photo.setFileName("테스트");
//
//        Photo savedPhoto = photoRepository.save(photo);
//        PhotoDto resPhotoDto = photoService.getPhoto(savedPhoto.getPhotoId());
//
//        assertEquals("테스트", resPhotoDto.getFileName());
//    }
//}