package com.squarecross.photoalbum.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.squarecross.photoalbum.Constants;
import com.squarecross.photoalbum.config.SecurityUtil;
import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.domain.Photo;
import com.squarecross.photoalbum.dto.AlbumDto;
import com.squarecross.photoalbum.dto.PhotoDto;
import com.squarecross.photoalbum.mapper.AlbumMapper;
import com.squarecross.photoalbum.mapper.PhotoMapper;
import com.squarecross.photoalbum.repository.AlbumRepository;
import com.squarecross.photoalbum.repository.MemberRepository;
import com.squarecross.photoalbum.repository.PhotoRepository;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletContext;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepository;
    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Value("${multipart.max-file-size}")
    private Long maxFileSize;

    private final String original_path = Constants.PATH_PREFIX + "/photos/original";
    private final String thumb_path = Constants.PATH_PREFIX + "/photos/thumb";

    public List<PhotoDto> getPhotos(long albumId, String keyword, String sort, String order) throws IOException{
        List<PhotoDto> photoDtos = new ArrayList<>();
        List<Photo> photos = null;
        Optional<Album> albumOpt = albumRepository.findById(albumId);

        if (albumOpt.isEmpty()) throw new EntityNotFoundException("앨범이 존재하지 않습니다.");
        checkIsOwner(albumOpt.get().getMember().getId());

        if (Objects.equals(sort, "byName")) {
            if (Objects.equals(order, "asc"))
                photos = photoRepository.findByAlbum_AlbumIdAndFileNameContainingOrderByFileNameAsc(albumId, keyword);
            else if (Objects.equals(order, "desc"))
                photos = photoRepository.findByAlbum_AlbumIdAndFileNameContainingOrderByFileNameDesc(albumId, keyword);
            else
                throw new IllegalArgumentException("알 수 없는 정렬 기준 입니다.");

        } else if (Objects.equals(sort, "byDate")) {
            if (Objects.equals(order, "asc"))
                photos = photoRepository.findByAlbum_AlbumIdAndFileNameContainingOrderByUploadedAtAsc(albumId, keyword);
            else if (Objects.equals(order, "desc"))
                photos = photoRepository.findByAlbum_AlbumIdAndFileNameContainingOrderByUploadedAtDesc(albumId, keyword);
            else
                throw new IllegalArgumentException("알 수 없는 정렬 기준 입니다.");
        } else {
            throw new IllegalArgumentException("알 수 없는 정렬 기준 입니다.");
        }



        for (Photo photo: photos) {
            PhotoDto photoDto = PhotoMapper.convertToDto(photo);

            try (InputStream in =  new FileInputStream(Constants.PATH_PREFIX + photoDto.getThumbUrl())){
                photoDto.setImageFile(IOUtils.toByteArray(in));
            }
            photoDtos.add(photoDto);
        }

        return photoDtos;
    }

    public PhotoDto getPhoto(long photoId) throws IOException{
        Optional<Photo> photoOpt = photoRepository.findById(photoId);
        if (photoOpt.isEmpty()) throw new EntityNotFoundException(String.format("이미지 아이디 '%d'로 조회되지 않았습니다.", photoId));
        checkIsOwner(photoOpt.get().getAlbum().getMember().getId());

        PhotoDto photoDto = PhotoMapper.convertToDto(photoOpt.get());

        try (InputStream in =  new FileInputStream(Constants.PATH_PREFIX + photoDto.getOriginalUrl())){
            photoDto.setImageFile(IOUtils.toByteArray(in));
        }

        return photoDto;
    }


    public PhotoDto savePhoto(MultipartFile file, Long albumId) throws IOException {

        if (file.getSize() > maxFileSize){
            throw new SizeLimitExceededException("이미지의 크기가 너무 큽니다.", file.getSize(), maxFileSize);
        }

        Optional<Album> albumDto = albumRepository.findById(albumId);
        if(albumDto.isEmpty()) throw new EntityNotFoundException("앨범이 존재하지 않습니다.");

        checkIsOwner(albumDto.get().getMember().getId());

        String fileName = file.getOriginalFilename();
        if (!isImage(fileName)) throw new IllegalArgumentException("유효하지 않은 형태의 파일입니다.");

        int fileSize = (int)file.getSize();
        fileName = getNextFileName(fileName, albumId);

        saveFile(file, albumId, fileName);

        Photo photo = new Photo();
        photo.setOriginalUrl("/photos/original/" + albumId + "/" + fileName);
        photo.setThumbUrl("/photos/thumb/" + albumId + "/" + fileName);
        photo.setFileName(StringUtils.stripFilenameExtension(fileName));
        photo.setFileSize(fileSize);
        photo.setAlbum(albumDto.get());

        Photo createdPhoto = photoRepository.save(photo);


        return PhotoMapper.convertToDto(createdPhoto);
    }

    public File getImageFile(Long photoId) throws AccessDeniedException{
        Optional<Photo> photoOpt = photoRepository.findById(photoId);
        if(photoOpt.isEmpty()) throw new EntityNotFoundException(String.format("사진 아이디 '%d'를 찾을 수 없습니다.", photoId));
        checkIsOwner(photoOpt.get().getAlbum().getMember().getId());

        return new File(Constants.PATH_PREFIX + photoOpt.get().getOriginalUrl());
    }

    public List<PhotoDto> changeAlbum(List<Long> photoIds, Long albumId) throws IOException{

        List<PhotoDto> photoDtos = new ArrayList<>();
        Optional<Album> albumOpt = albumRepository.findById(albumId);
        if (albumOpt.isEmpty()) throw new EntityNotFoundException(String.format("앨범 아이디 '%d'를 찾을 수 없습니다.", albumId));
        checkIsOwner(albumOpt.get().getMember().getId());

        String[] targetUrls = null;
        String newFileName = null;

        for (Long photoId : photoIds) {
            Optional<Photo> photoOpt = photoRepository.findById(photoId);
            if (photoOpt.isEmpty()) throw new EntityNotFoundException(String.format("사진 아이디 '%d'를 찾을 수 없습니다.", photoId));

            Photo photo = photoOpt.get();
            checkIsOwner(photo.getAlbum().getMember().getId());

            photo.setAlbum(albumOpt.get());

            newFileName = getNextFileName(
                    String.format(
                            "%s.%s", photo.getFileName(), StringUtils.getFilenameExtension(photo.getOriginalUrl())
                    ) , albumId);
            photo.setFileName(StringUtils.stripFilenameExtension(newFileName));

            targetUrls = moveImageFile(photo, albumId);
            photo.setOriginalUrl(targetUrls[0]);
            photo.setThumbUrl(targetUrls[1]);

            Photo savedPhoto =  photoRepository.save(photo);

            PhotoDto photoDto = PhotoMapper.convertToDto(savedPhoto);
            photoDtos.add(photoDto);
        }

        return photoDtos;
    }

    public void deletePhoto(long photoId) throws IOException{
        Optional<Photo> photoOpt = photoRepository.findById(photoId);
        checkIsOwner(photoOpt.get().getAlbum().getMember().getId());
        if (photoOpt.isEmpty()) throw new EntityNotFoundException(String.format("이미지 아이디 %d가 조회되지 않았습니다.", photoId));

        deleteImageFile(photoOpt.get());
        photoRepository.delete(photoOpt.get());
    }

    private String getNextFileName(String fileName, Long albumId) {
        String fileNameNoExt = StringUtils.stripFilenameExtension(fileName);
        String ext = StringUtils.getFilenameExtension(fileName);

        fileName = fileNameNoExt;

        Optional<Photo> photo = photoRepository.findByFileNameAndAlbum_AlbumId(fileNameNoExt, albumId);

        int count = 2;
        while(photo.isPresent()){
            fileName = String.format("%s (%d)", fileNameNoExt, count);
            photo = photoRepository.findByFileNameAndAlbum_AlbumId(fileName, albumId);
            count ++;
        }

        return String.format("%s.%s", fileName, ext);
    }


    private void saveFile(MultipartFile file, Long albumId, String fileName) throws IOException{
        try {
            String filePath = "/" + albumId + "/" + fileName;

            File originalFile = new File(original_path + "/" + filePath);
            File thumbFile = new File(thumb_path + "/" + filePath);

            String ext = StringUtils.getFilenameExtension(fileName);

            int orientation = 1;
            Metadata metadata;
            Directory directory;
            try (InputStream fis = file.getInputStream()) {
                metadata = ImageMetadataReader.readMetadata(fis);
                directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
                if(directory != null && directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION))
                    orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            }

            try (InputStream fis = file.getInputStream()) {
                BufferedImage img = ImageIO.read(fis);
                if(orientation != 1) {
                    img = rotateImage(img, orientation);
                }
                ImageIO.write(img, ext, originalFile);
            }

            try (InputStream fis = file.getInputStream()) {
                BufferedImage thumbImg = Scalr.resize(ImageIO.read(fis), Constants.THUMB_SIZE, Constants.THUMB_SIZE);
                if(orientation != 1) {
                    thumbImg = rotateImage(thumbImg, orientation);
                }
                ImageIO.write(thumbImg, ext, thumbFile);
            }


        } catch (Exception e) {
            throw new FileUploadException("Could not store the file. Error: " + e.getMessage());
        }
    }

    private boolean isImage(String fileName) {
        File checkFile = new File(fileName);

        String type;
        try {
            type = Files.probeContentType(checkFile.toPath());

        } catch (IOException e) {
            throw new RuntimeException("Could not probed content type. Error: " + e.getMessage());
        }

        if (!type.startsWith("image")) return false;
        return true;
    }

    private String[] moveImageFile(Photo photo, Long albumId) throws IOException{
        try{
            String ext = StringUtils.getFilenameExtension(photo.getOriginalUrl());
            String originalTargetUrl =  String.format("/photos/original/%d/%s.%s", albumId, photo.getFileName(), ext);
            String thumbTargetUrl =  String.format("/photos/thumb/%d/%s.%s", albumId, photo.getFileName(), ext);

            Files.move(Paths.get(Constants.PATH_PREFIX + photo.getOriginalUrl()),
                    Paths.get(Constants.PATH_PREFIX + originalTargetUrl));

            Files.move(Paths.get(Constants.PATH_PREFIX + photo.getThumbUrl()),
                    Paths.get(Constants.PATH_PREFIX + thumbTargetUrl));

            return new String[]{originalTargetUrl, thumbTargetUrl};
        } catch (IOException e) {
            throw new IOException("Could not move image file. Error: " + e.getMessage());
        }

    }

    private void deleteImageFile(Photo photo) throws IOException {
        try {
            Files.delete(Paths.get(Constants.PATH_PREFIX + photo.getOriginalUrl()));
            Files.delete(Paths.get(Constants.PATH_PREFIX + photo.getThumbUrl()));
        } catch (Exception e) {
            throw e;
        }

    }

//    private File convert(MultipartFile multipartFile) throws IOException{
//        File file= new File(multipartFile.getOriginalFilename());
//        file.createNewFile();
//        FileOutputStream fos = new FileOutputStream(file);
//        fos.write(multipartFile.getBytes());
//        fos.close();
//        return file;
//    }

    private BufferedImage rotateImage (BufferedImage bufferedImage, int orientation) {

        BufferedImage rotatedImage;

        if(orientation == 6 ) {
            rotatedImage = Scalr.rotate(bufferedImage, Scalr.Rotation.CW_90);
        } else if (orientation == 3) {
            rotatedImage = Scalr.rotate(bufferedImage, Scalr.Rotation.CW_180);
        } else if(orientation == 8) {
            rotatedImage = Scalr.rotate(bufferedImage, Scalr.Rotation.CW_270);
        } else {
            rotatedImage = bufferedImage;
        }

        return rotatedImage;
    }

    private void checkIsOwner(Long ownerId) throws AccessDeniedException {
        Long memberId = SecurityUtil.getLoginUserId();
        if (memberRepository.findById(memberId).isEmpty()) throw new UsernameNotFoundException("자격증명에 실패하였습니다.");
        if (!ownerId.equals(memberId)) throw new AccessDeniedException("잘못된 접근입니다.");
    }

}
