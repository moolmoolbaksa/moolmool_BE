package com.sparta.mulmul.barter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.mulmul.barter.barterDto.HotBarterDto;
import com.sparta.mulmul.barter.barterDto.QHotBarterDto;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sparta.mulmul.barter.QBarter.barter1;


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

}
