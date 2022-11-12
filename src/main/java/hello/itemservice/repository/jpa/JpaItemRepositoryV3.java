package hello.itemservice.repository.jpa;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.itemservice.domain.Item;
import hello.itemservice.domain.QItem;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static hello.itemservice.domain.QItem.*;

@Repository
@Transactional
public class JpaItemRepositoryV3 implements ItemRepository {

    private final EntityManager em;
    // 쿼리 DSL 사용시 필요
    private final JPAQueryFactory query;

    public JpaItemRepositoryV3(EntityManager em) {
        this.em = em;
        // 결과적으로 쿼리 dsl은 jpa의 jpql의 빌더 역할을 한다.
        // jdbc 켐플릿이랑 비슷하다고 보면 된다.
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public Item save(Item item) {
        em.persist(item);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        // 업데이트 코드
        // find에서 반환 클래스와 프라이머리 키를 인자로 받는다.
        Item findItem = em.find(Item.class, itemId);
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
        // 트랜젝션이 커밋되는 시점에서 디비로 반환이 된다.
    }

    @Override
    public Optional<Item> findById(Long id) {
        Item item = em.find(Item.class, id);
        return Optional.ofNullable(item);
    }

    // 동적 쿼리 구간
    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

//        QItem item = new QItem("i"); // i가 엘리어스

        BooleanBuilder builder = new BooleanBuilder();
        if(StringUtils.hasText(itemName)){
//            builder.and(item.i)
        }
        List<Item> result = query.select(item)
                .from(item)
                .where()
                .fetch();// fetch는 리스트
        return result;
    }
}
