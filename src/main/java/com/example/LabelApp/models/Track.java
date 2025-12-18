package com.example.LabelApp.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tracks")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"covers", "user", "favoritedBy", "comments", "musicFile"})
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String artists;
    private String genre;

    private String releaseDate;

    @Column(columnDefinition = "text")
    private String lyric;

    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cover> covers = new ArrayList<>();

    private Long previewCoverId;

    private LocalDateTime dateOfCreated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // === пользователи, добавившие этот трек в избранное ===
    @ManyToMany(mappedBy = "favorites")
    private Set<User> favoritedBy = new HashSet<>();

    // ===  комментарии под этим треком ===
    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @PrePersist
    private void init() {
        dateOfCreated = LocalDateTime.now();
    }

    public void addCoverToTrack(Cover cover) {
        cover.setTrack(this);
        covers.add(cover);
    }

    @OneToOne(mappedBy = "track", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MusicFile musicFile;

    public void setMusicFile(MusicFile musicFile) {
        musicFile.setTrack(this);
        this.musicFile = musicFile;
    }
}