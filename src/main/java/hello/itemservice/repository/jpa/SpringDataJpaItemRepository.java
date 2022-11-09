package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SpringDataJpaItemRepository extends JpaRepository<Item,Long> {

    List<Item> findByItemNameLike(String itemName);

    List<Item> findByPriceLessThanEqual(Integer price);

    // 쿼리 매서드
    List<Item> findByItemNameLikeAndPriceLessThanEqual(String itemName,Integer price);

    // 쿼리 직접 실행, jpql 직접 작성,:는 파라미터 바인딩
    @Query("select i from Item i where i.itemName like :itemName and i.price <= :price")
    List<Item> findItems(String itemName ,Integer price);
}
