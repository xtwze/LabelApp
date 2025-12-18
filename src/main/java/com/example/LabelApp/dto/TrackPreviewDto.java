// TrackPreviewDto.java (для списка на главной)
package com.example.LabelApp.dto;

import lombok.Data;

@Data
public class TrackPreviewDto {
    private Long id;
    private String title;
    private String artists;
    private Long previewCoverId;
    private String artistName;
}