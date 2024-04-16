package com.github.starnowski.spring.cloud.fun.gateway;

import com.github.starnowski.spring.cloud.fun.gateway.model.Item;
import com.github.starnowski.spring.cloud.fun.gateway.services.ItemService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;


@RestController
@Validated
public class ItemController {

    @Autowired
    private ItemService itemService;

    public ItemController(@Autowired ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/item/{id}")
    public ResponseEntity<Item> get(@Valid @PathVariable("id") @Size(max = 15) String id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleValidationExceptions(ConstraintViolationException ex) {
        StringBuilder errorMessages = new StringBuilder();
        ex.getConstraintViolations().forEach(error -> {
            errorMessages.append(error.getMessage()).append(". ");
        });
        return ResponseEntity.badRequest().body(errorMessages.toString());
    }
}
