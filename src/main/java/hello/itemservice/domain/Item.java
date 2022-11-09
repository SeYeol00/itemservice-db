package hello.itemservice.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity // jpa에서 관리하는구나라고 알 수 있다.
public class Item {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)//디비에서 값을 증가
    private Long id;

    @Column(name = "item_name",length = 10)
    private String itemName;

    @Column(name = "price")
    private Integer price;

    @Column(name = "quantity")
    private Integer quantity;

    // jpa는 기본 생성자가 꼭 필요하다.
    // 이것이 있어야 프록시 기능을 사용할 수 있다.
    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
