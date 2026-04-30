package com.coffeeshop.config;

import com.coffeeshop.entity.Category;
import com.coffeeshop.entity.Product;
import com.coffeeshop.repository.CategoryRepository;
import com.coffeeshop.repository.ProductRepository;
import com.coffeeshop.util.EntityDisplayUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@RequiredArgsConstructor
public class CatalogMetadataBackfillRunner implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void run(String... args) {
        backfillCategoryCodes();
        backfillProductCodes();
    }

    private void backfillCategoryCodes() {
        List<Category> updates = new ArrayList<>();
        for (Category category : categoryRepository.findAll()) {
            if (!StringUtils.hasText(category.getCategoryCode())) {
                category.setCategoryCode(EntityDisplayUtils.buildReadableCode(
                        "CAT",
                        category.getName(),
                        category.getId()));
                updates.add(category);
            }
        }

        if (!updates.isEmpty()) {
            categoryRepository.saveAll(updates);
        }
    }

    private void backfillProductCodes() {
        List<Product> updates = new ArrayList<>();
        for (Product product : productRepository.findAll()) {
            if (!StringUtils.hasText(product.getProductCode())) {
                product.setProductCode(EntityDisplayUtils.buildReadableCode(
                        "PRD",
                        product.getName(),
                        product.getId()));
                updates.add(product);
            }
        }

        if (!updates.isEmpty()) {
            productRepository.saveAll(updates);
        }
    }
}
