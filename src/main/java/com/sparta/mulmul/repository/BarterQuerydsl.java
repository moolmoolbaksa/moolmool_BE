package com.sparta.mulmul.repository;

import com.sparta.mulmul.dto.barter.HotBarterDto;

import java.util.List;

public interface BarterQuerydsl {

    List<HotBarterDto> findByHotBarter (int status);
}
