package com.squarecross.photoalbum.dto;

import java.util.Date;
import java.util.List;

public class AlbumDto {
    Long albumId;
    String albumName;
    Date createdAt;
    int count;

    Long memberId;

    List<byte[]> thumbUrls;

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<byte[]> getThumbUrls() {
        return thumbUrls;
    }

    public void setThumbUrls(List<byte[]> thumbUrls) {
        this.thumbUrls = thumbUrls;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
}
