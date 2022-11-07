package hello.itemservice.repository.mybatis;


import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;


// SQL 매퍼 생성
// 넘어오는 파라미터의 필드의 이름을 다 쓸 수 있다.
// 내부에서 매퍼가 구현체를 만들어서 xml을 사용한다.
@Mapper
public interface ItemMapper {

    void save(Item item);

    void update(@Param("id") Long id, @Param("updateParam") ItemUpdateDto updateParam);

    List<Item> findAll(ItemSearchCond itemSearch);

    // 이 어노테이션으로 직접 작성 가능하지만 비추한다.
    // SQL 인젝션 공격을 받을 수 있다.
   // @Select("select id, item_name, price, quantity from item where id=#{id}")
    Optional<Item> findById(Long id);
}
