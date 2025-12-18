package com.example.LabelApp.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ShowTrackDto {

    private Long id;
    private String title;
    private String artists;
    private String genre;
    private String releaseDate;
    private String lyric;

    private String audioFilePath;
    private String musicContentType;

    private Long previewCoverId;
    private String artistName;
    private LocalDateTime dateOfCreated;

    private List<CommentDto> comments = new ArrayList<>();

}