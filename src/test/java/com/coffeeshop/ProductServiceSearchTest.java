package com.coffeeshop;

import com.coffeeshop.entity.Product;
import com.coffeeshop.repository.ProductRepository;
import com.coffeeshop.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceSearchTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void fuzzySearchNormalizesVietnameseDCharacter() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("Đá Xay Bạc Hà");
        product.setDescription("Mint ice blended drink");
        product.setBasePrice(BigDecimal.valueOf(55000));

        when(productRepository.findAllWithDetails()).thenReturn(List.of(product));

        List<Product> results = productService.searchProductsFuzzy("da xay");

        assertEquals(1, results.size());
        assertEquals(product.getId(), results.get(0).getId());
    }
}
