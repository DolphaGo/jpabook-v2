package com.jpashop.dolphago.controller;

import com.jpashop.dolphago.domain.Book;
import com.jpashop.dolphago.domain.Item;
import com.jpashop.dolphago.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model){
        model.addAttribute("form",new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form){
        //가급적 Setter는 사용하지 말 것. 예제이므로 사용하는 것 뿐.
        //그리고 Entity와 Form은 분리하라.
        Book book=new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/";
    }

    @GetMapping("/items")
    public String items(Model model){
        List<Item> items = itemService.findItems();
        model.addAttribute("items",items);
        return "items/itemList";
    }

    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model){
        Book item =(Book) itemService.findOne(itemId);

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form",form);
        return "items/updateItemForm";
    }

    @PostMapping("items/{itemId}/edit")
    public String updateItem(@PathVariable("itemId") Long itemId, @ModelAttribute("form") BookForm form){
        //준영속 엔티티 : 기존 식별자를 가지고 있으면 준영속 엔티티로 볼 수 있음.
        //준영속 엔티티의 문제 " JPA가 관리를 안함.." -> "더티체킹이 안됨!"
        //현재 book은 준영속 상태입니다! 지금 2번 방법으로 업데이트 하는 중.
//        Book book=new Book();
//        book.setId(form.getId());
//        book.setName(form.getName());
//        book.setPrice(form.getPrice());
//        book.setStockQuantity(form.getStockQuantity());
//        book.setAuthor(form.getAuthor());
//        book.setIsbn(form.getIsbn());
//
//        //준영속 엔티티를 수정하는 2가지 방법
//        //1. 변경 감지 기능 사용, 2. 병합(merge) 사용
//        itemService.saveItem(book);
        itemService.updateItem(form.getId(),form.getName(),form.getPrice(), form.getStockQuantity()); //훨씬 좋은 설계, 또는 Dto를 활용할 수도 있음.
        return "redirect:/items";
    }
}
