package hello.itemservice.repository.jdbctemplate;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * NameParameterJdbcTemplate
 * 자바 빈 규약에 맞춰서 데이터베이스에 파라미터를 알아서 매핑 해준다.
 * 자바의 카멜 케이스 ==> 데이터 베이스의 스네이크 케이스(언더스코어)
 * 이것을 알아서 맞춰 주는 것이 이 탬플릿의 핵심 기능이다.
 */
@Slf4j
public class JdbcTemplateItemRepositoryV2 implements ItemRepository {

//    private final JdbcTemplate template;

    // 파라미터가 순서가 아니라 이름으로 바인딩함
    private final NamedParameterJdbcTemplate template;

    public JdbcTemplateItemRepositoryV2(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Item save(Item item) {
        String sql = "insert into item(item_name, price, quantity) " +
                "values(:itemName,:price,:quantity)";
        // 케이스 1
        SqlParameterSource param = new BeanPropertySqlParameterSource(item);

        // Jdbc 키 생성
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(sql,param,keyHolder);

        long key = keyHolder.getKey().longValue();
        item.setId(key);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        String sql = "update item set " +
                "item_name=:itemName, price=:price, quantity=:quantity " +
                "where id=?";

        // 케이스 2 -> 파라미터의 이름이 쿼리와 맞지 않을 때 직접 넣는 케이스
        SqlParameterSource param= new MapSqlParameterSource()
                .addValue("itemName", updateParam.getItemName())
                .addValue("price",updateParam.getQuantity())
                .addValue("quantity",updateParam.getQuantity())
                .addValue("id",itemId); // 이 부분이 별도로 필요하다.
        template.update(sql,param);
    }

    @Override
    public Optional<Item> findById(Long id) {
        String sql = "select id, item_name, price, quantity from item where id=?";
        try{
            // 파라미터를 맵으로 쓸 수 있다. 아이디를 파라미터로
            Map<String, Long> param = Map.of("id", id);
            //          템플릿이 알아서 반복문 돌려준다.
            Item item = template.queryForObject(sql,param, itemRowMapper());
            return Optional.of(item);
        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        // 검색 조건
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(cond);

        String sql = "select id, item_name, price, quantity from item";
        //동적 쿼리
        if (StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " where";
        }
        boolean andFlag = false;
        if (StringUtils.hasText(itemName)) {
            sql += " item_name like concat('%',:itemName,'%')";
            andFlag = true;
        }
        if (maxPrice != null) {
            if (andFlag) {
                sql += " and";
            }
            sql += " price <= :maxPrice";
        }
        log.info("sql={}", sql);
        return template.query(sql,param, itemRowMapper());
    }
    private RowMapper<Item> itemRowMapper() {

        // 여기서 알아서 매핑해줍니다..
        return BeanPropertyRowMapper.newInstance(Item.class); // camel 변환 지원
    }
}
