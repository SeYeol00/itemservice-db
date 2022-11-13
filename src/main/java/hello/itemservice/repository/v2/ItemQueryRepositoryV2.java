package hello.itemservice.repository.v2;


import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.itemservice.domain.Item;
import hello.itemservice.domain.QItem;
import hello.itemservice.repository.ItemSearchCond;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;

import java.util.List;

import static hello.itemservice.domain.QItem.*;

@Repository // 여기서는 동적쿼리만 처리한다. 아이템 서치 레포지토리라고 해도 좋다.
public class ItemQueryRepositoryV2 {

    private final JPAQueryFactory query;


    public ItemQueryRepositoryV2(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    public List<Item> findAll(ItemSearchCond cond){

        return query
                .select(item)
                .from(item)
                .where(
                        likeItemNames(cond.getItemName()),
                        maxPrice(cond.getMaxPrice())
                        )
                .fetch();
    }

    private BooleanExpression likeItemNames(String itemName){
        if(StringUtils.hasText(itemName)){
            return (item.itemName.like("%" + itemName +"%"));
        }
        else{
            return null;
        }
    }

    private BooleanExpression maxPrice(Integer maxPrice){
        if(maxPrice != null){ // less or equal
            return (item.price.loe(maxPrice));
        }
        else{
            return null;
        }
    }
}
