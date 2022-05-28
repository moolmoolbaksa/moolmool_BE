package com.sparta.mulmul.item;

import com.sparta.mulmul.item.Scrab;

import java.util.List;

public interface ScrabQuerydsl {

    List<Scrab> findAllItemById(Long itemId);
}
