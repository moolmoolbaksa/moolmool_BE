package com.sparta.mulmul.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.mulmul.dto.barter.HotBarterDto;
import com.sparta.mulmul.dto.barter.QHotBarterDto;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sparta.mulmul.model.QBarter.barter1;

@Repository
public class BarterRepositoryImpl implements BarterQuerydsl {

    private final JPAQueryFactory queryFactory;

    public BarterRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<HotBarterDto> findByHotBarter(int status) {
        return queryFactory
                .select(new QHotBarterDto(barter1.barter))
                .from(barter1)
                .where(barter1.status.eq(status))
                .limit(1000)
                .orderBy(barter1.sellerId.desc())
                .fetch();
    }

//    @Override
//    public List<BarterIAlltemListDto> findBybarterItem(Long barterId, Long userId) {
//        return queryFactory
//                .select(new QBarterItemListDto(
//                        item.id,
//                        item.title,
//                        item.itemImg,
//                        item.contents
//                ))
//                .from (barter1)
//                .join(item)on(barter1.buyerId.eq(item.bag.userId))
//                .
//    }
}
