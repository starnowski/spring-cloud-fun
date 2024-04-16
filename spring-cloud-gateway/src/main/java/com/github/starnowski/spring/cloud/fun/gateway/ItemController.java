package com.github.starnowski.spring.cloud.fun.gateway;

import com.github.starnowski.spring.cloud.fun.gateway.model.Item;
import com.github.starnowski.spring.cloud.fun.gateway.services.ItemService;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Validated
public class ItemController {

    private final ItemService itemService;

    public ItemController(@Autowired ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/item/{id}")
    public ResponseEntity<Item> get(@PathVariable("id") @Size(max = 15) String id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }
}
