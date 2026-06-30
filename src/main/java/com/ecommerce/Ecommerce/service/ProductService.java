package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.model.Product;
import com.ecommerce.Ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    public List<Product> addProduct(List<Product> products) {
        for(Product p:products){
            p.setThreshold((long)( p.getStock()* 0.25));
        }
        return productRepository.saveAll(products);
    }

    public void restock(Long productId, Long newStock) {
        Product product = productRepository.findById(productId).orElseThrow();
        product.incStock(newStock);
        product.setThreshold((long)(product.getStock()*0.25));
        productRepository.save(product);
    }
}
