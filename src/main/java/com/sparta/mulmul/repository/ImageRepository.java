package com.sparta.mulmul.repository;

import com.sparta.mulmul.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

    // 성훈 - 마이페이지 프로파일 수정
//    String findbyfileName(String fileName);
}

