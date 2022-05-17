package com.sparta.mulmul.repository;

import com.sparta.mulmul.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Location findByArea(String area);
}
