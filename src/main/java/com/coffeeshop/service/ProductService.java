package com.coffeeshop.service;

import com.coffeeshop.entity.Product;
import com.coffeeshop.entity.ProductSize;
import com.coffeeshop.repository.ProductRepository;
import com.coffeeshop.util.EntityDisplayUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @org.springframework.cache.annotation.Cacheable("products")
    public List<Product> getAllProducts() {
        return productRepository.findAllWithDetails();
    }

    @org.springframework.cache.annotation.Cacheable(value = "products", key = "#categoryId != null ? #categoryId : 'all'")
    public List<Product> getProductsByCategory(java.util.UUID categoryId) {
        if (categoryId == null) {
            return getAllProducts();
        }
        return productRepository.findByCategoryIdAndActiveTrue(categoryId);
    }

    public List<Product> getAllProductsAdmin() {
        return productRepository.findAllWithDetailsAll();
    }

    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return productRepository.findAllWithDetailsAll();
        }
        return productRepository.searchProducts(keyword);
    }

    public List<Product> searchProductsForMenu(String keyword, java.util.UUID categoryId) {
        return filterProductsFuzzy(getProductsByCategory(categoryId), keyword);
    }

    public org.springframework.data.domain.Page<Product> getProductsPaginated(
            org.springframework.data.domain.Pageable pageable) {
        return productRepository.findAllWithDetailsPaginated(pageable);
    }

    public org.springframework.data.domain.Page<Product> searchProductsPaginated(String keyword,
            org.springframework.data.domain.Pageable pageable) {
        return productRepository.searchProductsPaginated(keyword, pageable);
    }

    public org.springframework.data.domain.Page<Product> getProductsByStatusPaginated(boolean active,
            org.springframework.data.domain.Pageable pageable) {
        return productRepository.findByActiveWithDetailsPaginated(active, pageable);
    }

    public Optional<Product> getProductById(java.util.UUID id) {
        return productRepository.findByIdWithDetails(id);
    }

    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "products", allEntries = true)
    public Product saveProduct(@org.springframework.lang.NonNull Product product) {
        if (!StringUtils.hasText(product.getProductCode())) {
            long nextNumber = 1;
            String code;
            do {
                code = String.format("PRD-%05d", nextNumber);
                nextNumber++;
            } while (productRepository.findByProductCodeIgnoreCase(code).isPresent());
            product.setProductCode(code);
        }
        if (product.getSizes() != null) {
            for (ProductSize size : product.getSizes()) {
                size.setProduct(product);
            }
        }
        return productRepository.save(product);
    }

    public void deleteProduct(@org.springframework.lang.NonNull java.util.UUID id) {
        updateStatus(id, false);
    }

    @org.springframework.cache.annotation.CacheEvict(value = "products", allEntries = true)
    public void updateStatus(java.util.UUID id, boolean active) {
        productRepository.findById(id).ifPresent(product -> {
            product.setAvailable(active);
            productRepository.save(product);
        });
    }

    @org.springframework.cache.annotation.Cacheable(value = "products", key = "'search_' + #keyword")
    public List<Product> searchProductsFuzzy(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllProducts();
        }
        return filterProductsFuzzy(getAllProducts(), keyword);
    }

    private List<Product> filterProductsFuzzy(List<Product> products, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return products;
        }

        String[] queryTerms = normalizeSearchText(keyword).split("\\W+");

        Map<java.util.UUID, Double> scores = new HashMap<>();
        Map<java.util.UUID, Product> productMap = new HashMap<>();

        for (Product product : products) {
            double productScore = 0;

            String name = normalizeSearchText(product.getName());
            String description = normalizeSearchText(product.getDescription());

            for (String queryTerm : queryTerms) {
                if (queryTerm.length() < 2) {
                    continue;
                }

                if (name.contains(queryTerm)) {
                    productScore += 100.0;
                    if (name.equals(queryTerm)) {
                        productScore += 50.0;
                    }
                } else if (isFuzzyMatch(queryTerm, name)) {
                    productScore += 40.0;
                }


                if (description.contains(queryTerm)) {
                    productScore += 5.0;
                }
            }

            if (productScore > 0) {
                scores.put(product.getId(), productScore);
                productMap.put(product.getId(), product);
            }
        }

        if (scores.isEmpty()) {
            return List.of();
        }

        double maxScore = scores.values().stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
        double threshold = maxScore * 0.4;

        return scores.entrySet().stream()
                .filter(entry -> entry.getValue() >= threshold)
                .sorted((left, right) -> Double.compare(right.getValue(), left.getValue()))
                .map(entry -> productMap.get(entry.getKey()))
                .collect(Collectors.toList());
    }

    private String normalizeSearchText(String text) {
        if (text == null) {
            return "";
        }

        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase()
                .trim();

        return normalized.replace("\u0111", "d");
    }

    private boolean isFuzzyMatch(String queryTerm, String targetText) {
        if (targetText == null || targetText.isEmpty()) {
            return false;
        }
        if (queryTerm.length() <= 3) {
            return false;
        }

        String[] targetTokens = targetText.split("\\W+");
        for (String token : targetTokens) {
            if (token.length() < 3) {
                continue;
            }

            int distance = calculateLevenshteinDistance(queryTerm, token);
            int maxDistance = queryTerm.length() > 6 ? 2 : 1;
            if (distance <= maxDistance) {
                return true;
            }
        }

        return false;
    }

    private int calculateLevenshteinDistance(String left, String right) {
        int[][] dp = new int[left.length() + 1][right.length() + 1];
        for (int i = 0; i <= left.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= right.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= left.length(); i++) {
            for (int j = 1; j <= right.length(); j++) {
                int cost = left.charAt(i - 1) == right.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost);
            }
        }

        return dp[left.length()][right.length()];
    }
}
