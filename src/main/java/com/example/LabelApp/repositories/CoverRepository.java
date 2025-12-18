package com.example.LabelApp.repositories;

import com.example.LabelApp.models.Cover;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoverRepository extends JpaRepository<Cover, Long> {
}
