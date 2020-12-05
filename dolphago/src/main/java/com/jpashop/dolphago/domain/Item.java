package com.jpashop.dolphago.domain;

import com.jpashop.dolphago.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 싱글테이블 전략 -> Discriminator 필요(작성하지 않으면 Default "DTYPE")
public abstract class Item {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @OneToMany(mappedBy = "item")
    List<CategoryItem> categories=new ArrayList<>();

    //== Business Logic ==// : Entity 안에 비즈니스 로직을 넣으면 좋다. 데이터를 가지고 있는 곳에서 비즈니스 로직을 넣어주는 것이 응집도가 높을 것.
    //어떤 값을 변경할 때 Setter로 변경하지 말고, 비즈니스 로직으로 처리해야 한다.
    /**
     * stock 증가
     * @param quantity
     */
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    /**
     * stock 감소
     * @param quantity
     */
    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if(restStock <0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }


    public void change(String name, int price, int stockQuantity) {
        this.name=name;
        this.price=price;
        this.stockQuantity=stockQuantity;
    }
}
