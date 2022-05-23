package com.sparta.mulmul.repository;
import static com.sparta.mulmul.model.QItem.*;

import com.querydsl.jpa.impl.JPAQueryFactory;

import com.sparta.mulmul.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class ItemQuerydslImpl implements ItemQuerydsl {

    private final JPAQueryFactory queryFactory;

    public ItemQuerydslImpl(JPAQueryFactory jpaQueryFactory){
        this.queryFactory = jpaQueryFactory;
    }

    @Override
    public Page<Item> findAllItemOrderByCreatedAtDesc(Pageable pageable){
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
    public Page<Item> findAllItemByCategoryOrderByCreatedAtDesc(String category, Pageable pageable){
        List<Item> results = queryFactory
                .selectFrom(item)
                .where(item.status.eq(0).or(item.status.eq(1)).and(item.category.eq(category)))
                .orderBy(item.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(results, pageable, results.size());
    }

}
