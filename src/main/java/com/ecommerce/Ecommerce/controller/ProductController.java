package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.model.Product;
import com.ecommerce.Ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("product")
public class ProductController {

    @Autowired
    ProductService productService;


    @PostMapping("addProduct")
    public List<Product> addProduct(@RequestBody List<Product> products){
        return productService.addProduct(products);
    }

    @PostMapping("restock/{productId}/{newStock}")
    public void restock(@PathVariable Long productId,
                          @PathVariable Long  newStock){
        productService.restock(productId,newStock);
    }
}
