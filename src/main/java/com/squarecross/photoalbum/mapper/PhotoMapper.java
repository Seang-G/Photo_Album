package com.squarecross.photoalbum.mapper;

import com.squarecross.photoalbum.domain.Photo;
import com.squarecross.photoalbum.dto.PhotoDto;

import java.util.List;
import java.util.stream.Collectors;

public class PhotoMapper {

    public static PhotoDto convertToDto(Photo photo) {
        PhotoDto photoDto = new PhotoDto();
        photoDto.setPhotoId(photo.getPhotoId());
        photoDto.setFileName(photo.getFileName());
        photoDto.setThumbUrl(photo.getThumbUrl());
        photoDto.setUploadedAt(photo.getUploadedAt());
        photoDto.setOriginalUrl(photo.getOriginalUrl());
        photoDto.setFileSize(photo.getFileSize());

        return photoDto;
    }

    public static Photo convertToModel(PhotoDto photoDto) {
        Photo photo = new Photo();
        photo.setPhotoId(photoDto.getPhotoId());
        photo.setFileName(photoDto.getFileName());
        photo.setThumbUrl(photoDto.getThumbUrl());
        photo.setUploadedAt(photoDto.getUploadedAt());
        photo.setOriginalUrl(photoDto.getOriginalUrl());
        photo.setFileSize(photoDto.getFileSize());

        return photo;
    }

    public static List<PhotoDto> convertToPhotoList(List<Photo> photos) {
        return photos.stream().map(PhotoMapper::convertToDto).collect(Collectors.toList());
    }

}
