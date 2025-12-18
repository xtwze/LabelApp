package com.example.LabelApp.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "music_files")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "track")
public class MusicFile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;                  // оригинальное имя файла (например, "song.mp3")
    private String originalFileName;      // то же самое
    private Long size;                    // размер в байтах
    private String contentType;           // например, "audio/mpeg"


    private String filePath;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id")
    private Track track;
}