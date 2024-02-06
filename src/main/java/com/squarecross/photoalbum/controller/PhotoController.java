package com.squarecross.photoalbum.controller;

import com.squarecross.photoalbum.dto.AlbumDto;
import com.squarecross.photoalbum.dto.PhotoDto;
import com.squarecross.photoalbum.service.PhotoService;
import org.apache.catalina.util.IOTools;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.SizeLimitExceededException;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.*;
import java.nio.channels.MulticastChannel;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/albums/{albumId}/photos")
public class PhotoController {
    @Autowired
    private PhotoService photoService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<PhotoDto>> getPhotos(
            @PathVariable(value = "albumId") final long albumId,
            @RequestParam(value = "keyword", required = false, defaultValue = "") final String keyword,
            @RequestParam(value = "sort", required = false, defaultValue = "byDate") final String sort,
            @RequestParam(value = "order", required = false, defaultValue = "desc") final String order) throws IOException{

        List<PhotoDto> photos = photoService.getPhotos(albumId, keyword, sort, order);
        return new ResponseEntity<>(photos, HttpStatus.OK);
    }

    @RequestMapping(value = "/{photoId}", method = RequestMethod.GET)
    public ResponseEntity<PhotoDto> getPhotoInfo(@PathVariable(value = "photoId") final long photoId) throws IOException{
        PhotoDto photoDto =  photoService.getPhoto(photoId);

        return new ResponseEntity<>(photoDto, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<List<PhotoDto>> uploadPhotos(@PathVariable("albumId") final Long albumId,
                                                       @RequestParam(value = "photos") final List<MultipartFile> files ) throws IOException{
        List<PhotoDto> photoDtos = new ArrayList<>();

        for (MultipartFile file: files) {
            PhotoDto photoDto = photoService.savePhoto(file, albumId);
            photoDtos.add(photoDto);
        }
        return new ResponseEntity<>(photoDtos, HttpStatus.OK);
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void downloadPhotos(@RequestParam("photoIds") Long[] photoIds, HttpServletResponse response) throws IOException{
        try(OutputStream outputStream = response.getOutputStream()) {
            if (photoIds.length == 1) {
                File file = photoService.getImageFile(photoIds[0]);
                try (FileInputStream fileIS = new FileInputStream(file)){
                    IOUtils.copy(fileIS, outputStream);
                }
            } else {
                try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)){
                    response.setContentType("application/zip");
                    response.addHeader("Content-Disposition", "attachment; filename=\"Photos.zip\"");

                    for (long photoId: photoIds) {
                        File file = photoService.getImageFile(photoId);
                        zipOut.putNextEntry(new ZipEntry(file.getName()));

                        try (FileInputStream fileIS = new FileInputStream(file)){
                            IOUtils.copy(fileIS, zipOut);
                        }

                        zipOut.closeEntry();
                    }
                }

            }
        }
        catch (FileNotFoundException e) { throw new RuntimeException("Error"); }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseEntity<List<PhotoDto>> movePhoto(
            @RequestParam final List<Long> photoIds,
            @RequestParam final Long albumId) throws IOException {

        List<PhotoDto> resPhotoDtos = photoService.changeAlbum(photoIds, albumId);
        return new ResponseEntity<>(resPhotoDtos, HttpStatus.OK);
    }

    @RequestMapping(value = "/{photoId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deletePhoto(
            @PathVariable("photoId") final long photoId) throws IOException{

        photoService.deletePhoto(photoId);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deletePhotos(
            @RequestParam("photoIds") final List<Long> photoIds) throws IOException{
        for (Long photoId: photoIds)
            photoService.deletePhoto(photoId);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> badRequestErrorHandling(RuntimeException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> SizeLimitExceededErrorHandling(IOException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }


}
