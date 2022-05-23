package com.sparta.mulmul.repository;

import com.sparta.mulmul.model.Scrab;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ScrabQuerydsl {

    List<Scrab> findAllItemById(Long itemId);
}
