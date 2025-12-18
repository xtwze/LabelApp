package com.example.LabelApp.models;

import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "covers")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "track")
public class Cover {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;                  // оригинальное имя файла (например, "cover.jpg")
    private String originalFileName;      // то же самое, для совместимости
    private Long size;                    // размер в байтах
    private String contentType;           // например, "image/jpeg"

    // Относительный URL для доступа через контроллер
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id")
    private Track track;
}
