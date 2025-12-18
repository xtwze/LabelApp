package com.example.LabelApp.repositories;

import com.example.LabelApp.models.MusicFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicFileRepository extends JpaRepository<MusicFile, Long> {
}
