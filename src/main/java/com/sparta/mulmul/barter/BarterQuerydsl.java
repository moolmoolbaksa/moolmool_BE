package com.sparta.mulmul.barter;

import com.sparta.mulmul.barter.barterDto.HotBarterDto;

import java.util.List;

public interface BarterQuerydsl {

    List<HotBarterDto> findByHotBarter (int status);

}
