package com.sparta.mulmul.item;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import static com.sparta.mulmul.item.QScrab.*;
import java.util.List;

@Repository
public class ScrabQuerydslImpl implements  ScrabQuerydsl{

    private final JPAQueryFactory queryFactory;

    public ScrabQuerydslImpl(JPAQueryFactory jpaQueryFactory){
        this.queryFactory = jpaQueryFactory;
    }

    @Override
    public List<Scrab> findAllItemById(Long itemId){
        return queryFactory.selectFrom(scrab1)
                .where(scrab1.scrab.eq(true).and(scrab1.itemId.eq(itemId)))
                .fetch();
        }
    }
