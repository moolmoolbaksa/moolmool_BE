package com.sparta.mulmul.image;

import com.sparta.mulmul.image.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByImgUrl(String userImgUrl);
}

