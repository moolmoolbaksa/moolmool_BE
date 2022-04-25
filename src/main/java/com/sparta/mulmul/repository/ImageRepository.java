package com.sparta.mulmul.repository;


import com.sparta.mulmul.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
