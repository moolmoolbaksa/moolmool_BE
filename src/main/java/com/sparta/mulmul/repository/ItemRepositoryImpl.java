package com.sparta.mulmul.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.mulmul.dto.barter.BarterHotItemListDto;
import com.sparta.mulmul.dto.barter.BarterItemListDto;
import com.sparta.mulmul.dto.barter.QBarterHotItemListDto;
import com.sparta.mulmul.dto.barter.QBarterItemListDto;
import com.sparta.mulmul.dto.item.ItemUserResponseDto;
import com.sparta.mulmul.dto.item.QItemUserResponseDto;
import com.sparta.mulmul.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sparta.mulmul.model.QItem.item;
import static com.sparta.mulmul.model.QScrab.scrab1;

@Repository
public class ItemRepositoryImpl implements ItemQuerydsl {

    private final JPAQueryFactory queryFactory;

    public ItemRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<Item> findAllItemOrderByCreatedAtDesc(Pageable pageable) {
        List<Item> results = queryFactory
                .selectFrom(item)
                .where(item.status.eq(0).or(item.status.eq(1)))
                .orderBy(item.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(results, pageable, results.size());
    }

    @Override
    public Page<Item> findAllItemByCategoryOrderByCreatedAtDesc(String category, Pageable pageable) {
        List<Item> results = queryFactory
                .selectFrom(item)
                .where(item.status.eq(0).or(item.status.eq(1)).and(item.category.eq(category)))
                .orderBy(item.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(results, pageable, results.size());
    }

    // 성훈 - 마이페이지 0-2상태의 아이템정보를 dto에 담는다
    @Override
    public List<ItemUserResponseDto> findByMyPageItems(Long userId) {

        return queryFactory
                .select(new QItemUserResponseDto(
                        item.id,
                        item.itemImg,
                        item.status
                ))
                .from(item)
                .where(
                        item.bag.userId.eq(userId),
                        item.status.between(0, 2))
                .fetch();
    }

    // 성훈 - 찜하기를 한 아이템을 찾는다
    @Override
    public List<ItemUserResponseDto> findByMyScrabItems(Long userId) {

        return queryFactory
                .select(new QItemUserResponseDto(
                        item.id,
                        item.itemImg,
                        item.status
                ))
                .from(item)
                .join(scrab1).on(scrab1.itemId.eq(item.id))
                .fetchJoin()
                .distinct()
                .where(scrab1.userId.eq(userId), scrab1.scrab.eq(true))
                .orderBy(scrab1.modifiedAt.desc())
                .limit(3)
                .fetch();
    }

    @Override
    public BarterItemListDto findByBarterItems(Long itemId) {
        return queryFactory
                .select(new QBarterItemListDto(
                        item.id,
                        item.title,
                        item.itemImg,
                        item.contents
                ))
                .from(item)
                .where(item.id.eq(itemId))
                .fetchOne();
    }

    @Override
    public BarterHotItemListDto findByHotBarterItems(Long itemId) {
        return queryFactory
                .select(new QBarterHotItemListDto(
                        item.id,
                        item.title,
                        item.itemImg,
                        item.contents,
                        item.status
                ))
                .from(item)
                .where(item.id.eq(itemId))
                .fetchOne();
    }
}





