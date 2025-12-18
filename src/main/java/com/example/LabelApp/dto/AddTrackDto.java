package com.example.LabelApp.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AddTrackDto {

    private String title;
    private String artists;
    private String genre;
    private String releaseDate;
    private String lyric;

    private MultipartFile cover;
    private MultipartFile audio;
}