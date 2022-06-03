package com.sparta.mulmul.item;

import java.util.List;

public interface ScrabQuerydsl {

    List<Scrab> findAllItemById(Long itemId);

    List<Scrab> findAllScrab(Long userId);
}
