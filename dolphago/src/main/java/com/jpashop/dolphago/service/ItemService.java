package com.jpashop.dolphago.service;

import com.jpashop.dolphago.domain.Book;
import com.jpashop.dolphago.domain.Item;
import com.jpashop.dolphago.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional //readOnly라면 저장이 안됩니다!
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }


    //엔티티를 변경할 때는 항상 더티 체킹을 사용해라. merge는 사용하지 말 것.
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity){
        //setter는 지양해라. 의미가 있는 메서드 명을 해야한다. 예를들면 change...
        Item findItem=itemRepository.findOne(itemId); // findItem은 영속 상태
//        findItem.setPrice(price);
//        findItem.setName(name);
//        findItem.setStockQuantity(stockQuantity);
        //여기 끝까지 오면 Transactional에 의해 flush가 일어남. 변경감지가 됨.
        //updateQuery가 일어나서 DirtyChecking으로 자동으로 수정 쿼리가 나갑니다.
        findItem.change(name,price,stockQuantity); //이렇게 의미를 모아두어서 하나로 합치는 것이 더 더 좋은 설계
    }
}
