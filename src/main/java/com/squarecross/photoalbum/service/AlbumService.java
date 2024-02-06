package com.squarecross.photoalbum.service;

import com.squarecross.photoalbum.Constants;
import com.squarecross.photoalbum.config.SecurityUtil;
import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.domain.Member;
import com.squarecross.photoalbum.domain.Photo;
import com.squarecross.photoalbum.dto.AlbumDto;
import com.squarecross.photoalbum.mapper.AlbumMapper;
import com.squarecross.photoalbum.repository.AlbumRepository;
import com.squarecross.photoalbum.repository.MemberRepository;
import com.squarecross.photoalbum.repository.PhotoRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.io.*;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AlbumService {
    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private MemberRepository memberRepository;

    public AlbumDto getAlbum(Long albumId) throws AccessDeniedException{
        Optional<Album> albumOpt = albumRepository.findById(albumId);



        if (albumOpt.isPresent()) {
            checkIsOwner(albumOpt.get().getMember().getId());
            AlbumDto albumDto = AlbumMapper.convertToDto(albumOpt.get());
            albumDto.setCount(photoRepository.countByAlbum_AlbumId(albumId));
            return albumDto;
        }
        else throw new NoSuchElementException(String.format("앨범이 존재하지 않습니다.", albumId));
    }

//    public AlbumDto getAlbum(String albumName) throws RuntimeException, AccessDeniedException{
//        Long memberId = SecurityUtil.getLoginUserId();
//        Optional<Member> memberOpt = memberRepository.findById(memberId);
//        Optional<Album> albumOpt = albumRepository.findByAlbumName(albumName);
//
//        if (memberOpt.isEmpty()) throw new UsernameNotFoundException("자격증명에 실패하였습니다.");
//
//
//        if (albumOpt.isPresent()) {
//            AlbumDto albumDto = AlbumMapper.convertToDto(albumOpt.get());
//            albumDto.setCount(photoRepository.countByAlbum_AlbumName(albumName));
//            return albumDto;
//        }
//        else throw new EntityNotFoundException(String.format("앨범명 %s로 조회되지 않았습니다,", albumName));
//    }

    public List<AlbumDto> getAlbumList(String keyword, String sort, String order) throws IOException {
        List<Album> albums;
        Long memberId = SecurityUtil.getLoginUserId();
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) throw new UsernameNotFoundException("자격증명에 실패하였습니다.");

        if (Objects.equals(sort, "byName")){
            if (Objects.equals(order, "asc"))
                albums = albumRepository.findByMemberIdAndAlbumNameContainingOrderByAlbumNameAsc(memberId, keyword);

            else if (Objects.equals(order, "desc"))
                albums = albumRepository.findByMemberIdAndAlbumNameContainingOrderByAlbumNameDesc(memberId, keyword);

            else
                throw new IllegalArgumentException("알 수 없는 정렬 기준입니다.");

        } else if (Objects.equals(sort, "byDate")) {
            if (Objects.equals(order, "asc"))
                albums = albumRepository.findByMemberIdAndAlbumNameContainingOrderByCreatedAtAsc(memberId, keyword);

            else if (Objects.equals(order, "desc"))
                albums = albumRepository.findByMemberIdAndAlbumNameContainingOrderByCreatedAtDesc(memberId, keyword);
            else
                throw new IllegalArgumentException("알 수 없는 정렬 기준입니다.");

        } else {
            throw new IllegalArgumentException("알 수 없는 정렬 기준입니다.");
        }

        List<AlbumDto> albumDtos =  AlbumMapper.convertToDtoList(albums);

        for (AlbumDto albumDto: albumDtos){
            List<Photo> top4 = photoRepository.findTop4ByAlbum_AlbumIdOrderByUploadedAtDesc(albumDto.getAlbumId());
            List<byte[]> thumbUrls = new ArrayList<>();
            for (Photo photo: top4) {
                try (InputStream in =  new FileInputStream(Constants.PATH_PREFIX + photo.getThumbUrl())){
                    thumbUrls.add(IOUtils.toByteArray(in));
                }
            }
            albumDto.setThumbUrls(thumbUrls);
            albumDto.setCount(photoRepository.countByAlbum_AlbumId(albumDto.getAlbumId()));
        }

        return albumDtos;
    }

    public AlbumDto createAlbum(AlbumDto albumDto) throws IOException, RuntimeException {
        Long memberId = SecurityUtil.getLoginUserId();
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) throw new UsernameNotFoundException("자격증명에 실패하였습니다.");

        Album album = AlbumMapper.convertToModel(albumDto);
        album.setMember(memberOpt.get());
        albumRepository.save(album);

        createAlbumDirectories(album);
        return AlbumMapper.convertToDto(album);
    }

    public void deleteAlbum(long albumId) throws IOException {
        Optional<Album> albumOpt = albumRepository.findById(albumId);
        checkIsOwner(albumOpt.get().getMember().getId());

        deleteAlbumDirectories(albumOpt.get());
        albumRepository.delete(albumOpt.get());
    }

    public AlbumDto changeAlbumName(long albumId, AlbumDto albumDto) throws AccessDeniedException{
        Optional<Album> albumOpt = albumRepository.findById(albumId);
        albumOpt.orElseThrow(() -> new NoSuchElementException(String.format("해당 앨범이 존재하지 않습니다.")));
        checkIsOwner(albumOpt.get().getMember().getId());

        albumOpt.get().setAlbumName(albumDto.getAlbumName());
        Album savedAlbum = albumRepository.save(albumOpt.get());

        return AlbumMapper.convertToDto(savedAlbum);
    }

    private void createAlbumDirectories(Album album) throws IOException {
        Files.createDirectories(Paths.get(
                Constants.PATH_PREFIX + "/photos/original/" + album.getAlbumId()
        ));
        Files.createDirectories(Paths.get(
                Constants.PATH_PREFIX + "/photos/thumb/" + album.getAlbumId()
        ));
    }

    private void deleteAlbumDirectories(Album album) throws IOException {
        File originalDir = new File(Constants.PATH_PREFIX + "/photos/original/" + album.getAlbumId());
        for (File photo: originalDir.listFiles()) {
            photo.delete();
        }

        File thumbDir = new File(Constants.PATH_PREFIX + "/photos/thumb/" + album.getAlbumId());
        for (File thumbnail: thumbDir.listFiles()) {
            thumbnail.delete();
        }

        Files.delete(Paths.get(Constants.PATH_PREFIX + "/photos/original/" + album.getAlbumId()));
        Files.delete(Paths.get(Constants.PATH_PREFIX + "/photos/thumb/" + album.getAlbumId()));


    }

    private void checkIsOwner(Long ownerId) throws AccessDeniedException{
        Long memberId = SecurityUtil.getLoginUserId();
        if (!ownerId.equals(memberId)) throw new AccessDeniedException("잘못된 접근입니다.");
    }

}
