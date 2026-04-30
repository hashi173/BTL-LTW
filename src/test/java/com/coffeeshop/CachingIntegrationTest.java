package com.coffeeshop;

import com.coffeeshop.entity.Product;
import com.coffeeshop.repository.ProductRepository;
import com.coffeeshop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.cache.type=simple")
class CachingIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private CacheManager cacheManager;

    @MockBean
    private ProductRepository productRepository;

    @BeforeEach
    void clearProductCache() {
        Cache cache = cacheManager.getCache("products");
        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    void testProductCaching() {
        Product product = new Product();
        product.setId(java.util.UUID.randomUUID());
        product.setName("Cached Coffee");
        when(productRepository.findAllWithDetails()).thenReturn(Collections.singletonList(product));

        List<Product> firstRead = productService.getAllProducts();
        List<Product> secondRead = productService.getAllProducts();

        assertNotNull(firstRead);
        assertNotNull(secondRead);
        verify(productRepository, times(1)).findAllWithDetails();
    }
}
