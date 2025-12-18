package com.example.LabelApp.repositories;

import com.example.LabelApp.models.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackRepository extends JpaRepository<Track, Long> {

    // Точный поиск по названию (оставляем для совместимости, если используется где-то)
    List<Track> findByTitle(String title);

    // поиск с игнорированием регистра и частичным совпадением
    List<Track> findByTitleContainingIgnoreCase(String title);
}