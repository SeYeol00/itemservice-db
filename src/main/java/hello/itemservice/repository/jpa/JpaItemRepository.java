package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;



@Slf4j
@Repository // 이게 붙은 클래스는 예외변환 aop의 대상이 된다.
@Transactional
@RequiredArgsConstructor
public class JpaItemRepository implements ItemRepository {

    // 이 빈을 주입받아야 jpa를 쓸 수 있다.
    // 자바 컬랙션이라고 생각하면 편하다.
    // jpa의 핵심 콕표가 결국 디비를 자바 컬랙션처럼 사용하기 위함이다.
    // 결국 컬랙션 처럼 쓰는게 중요하다.
    private final EntityManager em;


    @Override
    public Item save(Item item) {

        // 저장 코드, 매니저가 알아서 해준다.
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

        // 조회코드, 반환 타입을 먼저 받고 그 다음 인자를 넣는다. 프라이머리
        Item item = em.find(Item.class, id);
        return Optional.ofNullable(item);
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        // 여러가지 조건이나 많은 인스턴스를 가져올 때는 jpql을 쓴다.
        // 여기서 i는 Item을 뜻한다.
        String jpql = "select i from Item i";
        // 여기서 Item은 엔티티 어노테이션을 붙인 객체를 뜻한다.
        // jpql의 장점은 이렇게 엔티티를 sql 엔티티처럼 사용가는 하다는 점이다.

        // 아래는 동적 쿼리
        // 지저분하다...
        Integer maxPrice = cond.getMaxPrice();
        String itemName = cond.getItemName();
        if (StringUtils.hasText(itemName) || maxPrice != null) {
            jpql += " where";
        }
        boolean andFlag = false;
        if (StringUtils.hasText(itemName)) {
            jpql += " i.itemName like concat('%',:itemName,'%')";
            andFlag = true;
        }
        if (maxPrice != null) {
            if (andFlag) {
                jpql += " and";
            }
            jpql += " i.price <= :maxPrice";
        }
        log.info("jpql={}", jpql);
        TypedQuery<Item> query = em.createQuery(jpql, Item.class);
        if (StringUtils.hasText(itemName)) {
            query.setParameter("itemName", itemName);
        }
        if (maxPrice != null) {
            query.setParameter("maxPrice", maxPrice);
        }
        return query.getResultList();
    }
}
