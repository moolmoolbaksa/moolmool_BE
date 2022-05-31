package com.sparta.mulmul.barter;

import com.sparta.mulmul.barter.barterDto.BarterAcceptorCntDto;
import com.sparta.mulmul.barter.barterDto.BarterRequesterCntDto;
import com.sparta.mulmul.barter.barterDto.HotBarterDto;

import java.util.List;

public interface BarterQuerydsl {

    List<HotBarterDto> findByHotBarter (int status);

    Long findByMyAcceptorCnt(Long userId);

    Long findByMyRequestorCnt(Long userId);

//    BarterAcceptorCntDto findByMyBarterCnt(Long userId);
}
